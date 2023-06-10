# REST API - APP BaGi

# API Endpoint

# Register

**Path :**

> /register

**Method :**

> `POST`

**Request Body :**

> - nama as `string`
> - email as `string`
> - phone as `string`
> - username as `string`, must be unique
> - password as `string`, must be at least 8 characters

**Response :**

```json
{
  "error": false,
  "message": "User registered successfully!"
}
```

**Error Response :**

```json
{
    "error": true,
    "message": "Email or Username already exist"
}
{
    "error": true,
    "message": "Password must be at least 8 characters long!"
}
```

# Login

**Path :**

> /login

**Method :**

> `POST`

**Request Body :**

> - email as `string`
> - password as `string`

**Response :**

```json
{
  "error": false,
  "loginResult": {
    "nama": "Indah",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImpvaG5kb2UifQ.5_HcBc8Qzc365MImOXwWMze8cip__Hu-pbwNEXrAEX4",
    "userId": 35
  },
  "message": "success"
}
```

**Errorr Response :**

```json
{
  "error": true,
  "message": "Invalid email or password!"
}
```

# Profile

**Path :**

> /profile

**Method :**

> `GET`

**Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
  "error": false,
  "message": "User profile retrieved successfully!",
  "profile": {
    "avatar": "https://storage.googleapis.com/zurv-bucket/gambar1.jpg",
    "email": "indah@example.com",
    "id": 6,
    "loc": "Kenten, Banyuasin",
    "nama": "indah",
    "phone": "0895",
    "username": "indah123"
  }
}
```

**Edit Profile**
**Path :**

> /profile

**Method :**

> `PUT`

**Header :**

> `Authorization` : `Bearer <token>` > `Content-Type`: `multipart/form-data`

**Request Body :**
add new data:

> - nama as `string`
> - phone as `string`
> - loc as `string`
> - avatar as file `png`, `jpg`, `jpeg`

**Response :**

```json
{
  "error": false,
  "message": "Profile updated successfully!",
  "profile": {
    "avatar": "https://storage.googleapis.com/zurv-bucket/gambar1.jpg",
    "email": "indah@example.com",
    "id": 6,
    "loc": "Kenten, Banyuasin",
    "nama": "indah",
    "phone": "0895",
    "username": "indah123"
  }
}
```

# Get All Items

**Path :**

> /items

**Parameters**

> - page as `int`, optional
> - size as `int`, optional

**Method :**

> `GET`

**Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "error": false,
    "items": [
        {
            "createAt": "2023-05-31 15:30:34",
            "description": "Baju ini masih layak pakai, saya baru beli bulan kemarin, ukurannya XL.",
            "id": 1,
            "kategori": "Baju",
            "loc": "Kenten, Banyuasin",
            "name": "indah",
            "nohp": "0895",
            "photoItems": [
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png",
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png",
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png"
            ],
            "photoUrl": "https://storage.googleapis.com/zurv-bucket/gambar1.jpg",
            "title": "Baju"
        }
    ],
    "message": "Items retrieved successfully"
}
```

# Detail Item

**Path :**

> /items/<int:item_id>

**Method :**

> `GET`

**Header :**

> `Authorization` : `Bearer <token>`

**Response :**

```json
{
    "error": false,
    "item": {
        "createAt": "2023-05-31 15:30:34",
        "description": "Baju ini masih layak pakai, saya baru beli bulan kemarin, ukurannya XL.",
        "id": 1,
        "kategori": "Baju",
        "loc": "Kenten, Banyuasin",
        "name": "indah",
        "nohp": "0895",
        "photoItems": [
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png",
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png",
                "https://storage.googleapis.com/zurv-bucket/00053543-removebg-preview.png"
        ],
        "photoUrl": "https://storage.googleapis.com/zurv-bucket/gambar1.jpg",
        "title": "Baju"
    },
    "message": "Items retrieved successfully"
}
```

# Upload item
**Path :**

> /upload_item

**Method :**

> `POST`

**Header :**

> `Authorization` : `Bearer <token>` > `Content-Type`: `multipart/form-data`

**Request Body :**
add new data:

> - title as `string`
> - description as `text`
> - category as `string`
> - images as file `png`, `jpg`, `jpeg`

**Response :**

```json
{
    "error": false,
    "message": "Item uploaded successfully!"
}
```
