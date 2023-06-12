from flask import Flask, request, jsonify, send_from_directory
from flask_bcrypt import Bcrypt
import jwt
import mysql.connector
from functools import wraps
from werkzeug.utils import secure_filename
import os
from waitress import serve
from dotenv import load_dotenv
from google.cloud import storage
from datetime import datetime
import pytz
from PIL import Image
import io

# Memuat variabel lingkungan dari file .env
load_dotenv()

app = Flask(__name__)
app.config['SECRET_KEY'] = os.environ.get("SECRET_KEY")  # Ganti dengan kunci rahasia Anda sendiri
bcrypt = Bcrypt(app)

config = {
    "user": os.environ.get("DB_USER"),
    "password": os.environ.get("DB_PASSWORD"),
    "host": os.environ.get("DB_HOST"),
    "database": os.environ.get("DB_DATABASE"),
    "unix_socket": os.environ.get("DB_UNIX_SOCKET")
}

db = mysql.connector.connect(**config)

# Decorator untuk memeriksa keaslian token
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            bearer_token = auth_header.split(' ')[1] if len(auth_header.split(' ')) > 1 else None

            if bearer_token:
                token = bearer_token

        if not token:
            return jsonify({'message': 'Token is missing!'}), 401

        try:
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user = data['email']
        except jwt.ExpiredSignatureError:
            return jsonify({'message': 'Token has expired!'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'message': 'Token is invalid!'}), 401

        return f(current_user, *args, **kwargs)

    return decorated
    
# Fungsi untuk mengupload file ke Google Cloud Storage
def upload_file_to_storage(file, filename):
    # Inisialisasi klien Google Cloud Storage
    client = storage.Client()

    # Pilih bucket yang ingin digunakan
    bucket_name = 'zurvin-bucket'
    bucket = client.bucket(bucket_name)

    # Buat objek blob dengan nama file yang diinginkan
    blob = bucket.blob(filename)

    # Upload file ke Google Cloud Storage
    blob.upload_from_file(file)

    # Dapatkan tautan publik ke file yang diupload
    url = blob.public_url

    return url

# Fungsi untuk memeriksa apakah tipe file diizinkan
def allowed_file(filename, allowed_extensions):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in allowed_extensions

# Fungsi untuk resize image
def resize_image(image, max_size):
    img = Image.open(image)
    
    # Ubah mode gambar dari RGBA menjadi RGB
    if img.mode == 'RGBA':
        img = img.convert('RGB')
    
    img.thumbnail(max_size, Image.ANTIALIAS)
    img_bytes = io.BytesIO()
    img.save(img_bytes, format='JPEG')
    img_bytes.seek(0)
    return img_bytes

# Route untuk testing
@app.route('/')
def home():
    return "Welcome to the API!"

# Route untuk registrasi
@app.route('/register', methods=['POST'])
def register():
    try:
        db.start_transaction()

        nama = request.form.get('nama')
        email = request.form.get('email')
        phone = request.form.get('phone')
        username = request.form.get('username')
        password = request.form.get('password')

        if nama and email and phone and username and password:
            if len(password) < 8:
                return jsonify({'error': True, 'message': 'Password must be at least 8 characters long!'}), 400

            cursor = db.cursor()
            try:
                hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
                cursor.execute("INSERT INTO users (nama, email, phone, username, password) VALUES (%s, %s, %s, %s, %s)",
                               (nama, email, phone, username, hashed_password))
                db.commit()
            except mysql.connector.IntegrityError as err:
                error_message = str(err)
                if 'username' in error_message or 'email' in error_message:
                    return jsonify({'error': True, 'message': 'Email or Username already exist!'}), 400

            return jsonify({'error': False, 'message': 'User registered successfully!'})

        return jsonify({'error': True, 'message': 'Invalid data!'}), 400

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

# Route untuk login
@app.route('/login', methods=['POST'])
def login():
    try:
        db.start_transaction()

        email = request.form.get('email')
        password = request.form.get('password')

        if email and password:
            cursor = db.cursor()
            cursor.execute("SELECT * FROM users WHERE email=%s", (email,))
            user = cursor.fetchone()

            if user and bcrypt.check_password_hash(user[5], password):
                token = jwt.encode({'email': user[4]}, app.config['SECRET_KEY'], algorithm='HS256')
                login_result = {
                    'userId': user[0],
                    'name': user[1],
                    'token': token
                }
                return jsonify({'error': False, 'message': 'success', 'loginResult': login_result})

        return jsonify({'error': True, 'message': 'Invalid email or password!'})

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

# Route untuk profile
@app.route('/profile', methods=['GET'])
@token_required
def get_profile(current_user):
    try:
        db.start_transaction()

        cursor = db.cursor()
        cursor.execute("SELECT * FROM users WHERE username=%s", (current_user,))
        user = cursor.fetchone()

        if user:
            user_data = {
                'id': user[0],
                'nama': user[1],
                'email': user[2],
                'phone': user[3],
                'username': user[4],
                'loc' : user[6],
                'avatar' : user[7]
            }
            return jsonify({'error': False, 'message': 'User profile retrieved successfully', 'profile': user_data}), 200
        else:
            return jsonify({'error': True, 'message': 'User not found'}), 404

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()


# Route untuk edit profile
@app.route('/profile', methods=['PUT'])
@token_required
def edit_profile(current_user):
    try:
        db.start_transaction()

        cursor = db.cursor()

        # Mengambil data profil pengguna yang akan diubah
        cursor.execute("SELECT * FROM users WHERE username=%s", (current_user,))
        user = cursor.fetchone()

        if not user:
            return jsonify({'message': 'User not found'}), 404

        # Mendapatkan data baru dari body permintaan
        new_username = request.form.get('username', user[4])

        if new_username != current_user:
            # Periksa apakah username yang baru sudah digunakan oleh pengguna lain
            cursor.execute("SELECT * FROM users WHERE username=%s", (new_username,))
            existing_user = cursor.fetchone()

            if existing_user:
                return jsonify({'error': True, 'message': 'Username already exists!'}), 400

        # Lanjutkan dengan pembaruan data profil pengguna
        data = request.form
        new_nama = data.get('nama', user[1])
        new_phone = data.get('phone', user[3])
        new_loc = data.get('loc', user[6])

        # Periksa apakah file avatar ada dalam request
        if 'avatar' in request.files:
            avatar = request.files['avatar']

            # Periksa apakah file avatar memiliki nama file dan memiliki ekstensi yang diperbolehkan
            if avatar.filename != '' and allowed_file(avatar.filename, {'png', 'jpg', 'jpeg', 'gif'}):
                # Resize image avatar
                resized_avatar = resize_image(avatar, max_size=(1024, 1024))

                # Simpan file avatar ke Google Cloud Storage
                filename = secure_filename(avatar.filename)
                avatar_url = upload_file_to_storage(resized_avatar, filename)
                new_avatar = avatar_url
            else:
                new_avatar = user[7]
        else:
            new_avatar = user[7]

        # Memperbarui data profil pengguna
        cursor.execute("UPDATE users SET username=%s, nama=%s, phone=%s, loc=%s, avatar=%s WHERE username=%s",
                       (new_username, new_nama, new_phone, new_loc, new_avatar, current_user))
        db.commit()

        # Mengambil data profil yang telah diperbarui
        cursor.execute("SELECT * FROM users WHERE username=%s", (new_username,))
        updated_user = cursor.fetchone()

        user_data = {
            'id': updated_user[0],
            'nama': updated_user[1],
            'email': updated_user[2],
            'phone': updated_user[3],
            'username': updated_user[4],
            'loc': updated_user[6],
            'avatar': updated_user[7]
        }

        return jsonify({'error': False, 'message': 'Profile updated successfully!', 'profile': user_data})

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

# Route untuk get all items (home)
@app.route('/items', methods=['GET'])
@token_required
def get_items(current_user):
    try:
        db.start_transaction()

        # Menampilkan semua item
        page = int(request.args.get('page', 1))
        size = int(request.args.get('size', 10))
        page = max(1, page)
        size = max(1, size)
        offset = (page - 1) * size

        cursor = db.cursor()
        cursor.execute("SELECT items.id, items.title, items.deskripsi, items.images, items.category, items.postDate, users.nama, users.loc, users.phone, users.avatar FROM items \
                        INNER JOIN users ON items.user_id = users.id \
                        LIMIT %s OFFSET %s", (size, offset))
        items = cursor.fetchall()

        item_list = []
        for item in items:
            item_data = {
                'id': item[0],
                'title': item[1],
                'description': item[2],
                'photoItems': item[3],
                'kategori': item[4],
                'createAt': item[5].strftime('%Y-%m-%d %H:%M:%S'),
                'name': item[6],
                'loc': item[7],
                'nohp': item[8],
                'photoUrl': item[9]
            }
            item_list.append(item_data)

        message = 'Items retrieved successfully'

        return jsonify({'error': False, 'message': message, 'items': item_list}), 200

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()


# Route untuk detail/deskripsi item
@app.route('/items/<int:item_id>', methods=['GET'])
@token_required
def get_item_by_id(current_user, item_id):
    try:
        db.start_transaction()

        cursor = db.cursor()
        cursor.execute("SELECT items.id, items.title, items.deskripsi, items.images, items.category, items.postDate, users.nama, users.loc, users.phone, users.avatar FROM items \
                       INNER JOIN users ON items.user_id = users.id \
                        WHERE items.id = %s", (item_id,))
        item = cursor.fetchone()

        if item:
            item_data = {
                'id': item[0],
                'title': item[1],
                'description': item[2],
                'photoItems': item[3],
                'kategori': item[4],
                'createAt': item[5].strftime('%Y-%m-%d %H:%M:%S'),
                'name': item[6],
                'loc': item[7],
                'nohp': item[8],
                'photoUrl': item[9] 
            }
            return jsonify({'error': False, 'message': 'Items retrieved successfully', 'item': item_data}), 200
        else:
            return jsonify({'error': True, 'message': 'Item not found'}), 404

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

@app.route('/upload_item', methods=['POST'])
@token_required
def upload_item(current_user):
    try:
        db.start_transaction()

        # Mendapatkan data dari body permintaan
        title = request.form.get('title')
        description = request.form.get('description')
        category = request.form.get('category')
        image = request.files.get('image')  # Change to get a single file instead of a list

        # Periksa apakah semua input diberikan
        if not title or not description or not category or not image:
            return jsonify({'error': True, 'message': 'Missing input data!'}), 400

        # Periksa apakah file gambar memiliki nama file dan memiliki ekstensi yang diperbolehkan
        allowed_extensions = {'png', 'jpg', 'jpeg', 'gif'}
        if not allowed_file(image.filename, allowed_extensions):
            return jsonify({'error': True, 'message': 'Invalid file extension!'}), 400

        # Simpan gambar ke Google Cloud Storage
        filename = secure_filename(image.filename)

        # Resize image item
        resized_image = resize_image(image, max_size=(1024, 1024))

        image_url = upload_file_to_storage(resized_image, filename)

        # Ambil ID dari current_user
        cursor = db.cursor()
        cursor.execute("SELECT id FROM users WHERE username=%s", (current_user,))
        user_id = cursor.fetchone()[0]

        # Dapatkan timestamp saat ini dengan zona waktu Jakarta
        current_time = datetime.now(pytz.timezone('Asia/Jakarta')).strftime('%Y-%m-%d %H:%M:%S')

        # Simpan informasi item ke database dengan timestamp sesuai dengan zona waktu Jakarta
        cursor.execute("INSERT INTO items (title, deskripsi, images, category, user_id, postDate) VALUES (%s, %s, %s, %s, %s, %s)",
                       (title, description, image_url, category, user_id, current_time))
        db.commit()

        return jsonify({'error': False, 'message': 'Item uploaded successfully!'}), 200

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

# Route untuk search items
@app.route('/items/search', methods=['GET'])
@token_required
def search_items(current_user):
    try:
        db.start_transaction()

        keyword = request.args.get('keyword')

        if not keyword:
            return jsonify({'error': True, 'message': 'Keyword parameter is required'}), 400

        cursor = db.cursor()
        cursor.execute("SELECT items.id, items.title, items.deskripsi, items.images, items.category, items.postDate, users.nama, users.loc, users.phone, users.avatar FROM items \
                        INNER JOIN users ON items.user_id = users.id \
                        WHERE items.title LIKE %s", ('%' + keyword + '%',))
        items = cursor.fetchall()

        item_list = []
        for item in items:
            item_data = {
                'id': item[0],
                'title': item[1],
                'description': item[2],
                'photoItems': item[3],
                'kategori': item[4],
                'createAt': item[5].strftime('%Y-%m-%d %H:%M:%S'),
                'name': item[6],
                'loc': item[7],
                'nohp': item[8],
                'photoUrl': item[9]
            }
            item_list.append(item_data)

        message = 'Search results retrieved successfully'

        return jsonify({'error': False, 'message': message, 'items': item_list}), 200

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()


# Route untuk mendapatkan item berdasarkan current user_id
@app.route('/items/user', methods=['GET'])
@token_required
def get_items_by_user_id(current_user):
    try:
        db.start_transaction()

        user_id = int(current_user['id'])

        cursor = db.cursor()
        cursor.execute("SELECT items.id, items.title, items.deskripsi, items.images, items.category, items.postDate, users.nama, users.loc, users.phone, users.avatar FROM items \
                       INNER JOIN users ON items.user_id = users.id \
                       WHERE users.id = %s", (user_id,))
        items = cursor.fetchall()

        if items:
            all_items = []
            for item in items:
                item_data = {
                    'id': item[0],
                    'title': item[1],
                    'description': item[2],
                    'photoItems': item[3].split(',') if item[3] else [],
                    'kategori': item[4],
                    'createAt': item[5].strftime('%Y-%m-%d %H:%M:%S'),
                    'name': item[6],
                    'loc': item[7],
                    'nohp': item[8],
                    'photoUrl': item[9] 
                }
                all_items.append(item_data)
            
            return jsonify({'error': False, 'message': 'Items retrieved successfully', 'items': all_items}), 200
        else:
            return jsonify({'error': True, 'message': 'Items not found'}), 404

    except Exception as e:
        db.rollback()
        return jsonify({'error': True, 'message': str(e)}), 500
    finally:
        db.commit()

if __name__ == '__main__':
    serve(app, host="0.0.0.0", port=int(os.environ.get('PORT', 8080)))
