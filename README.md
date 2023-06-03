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

**Errorr Response :**

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

> /Profile

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
    "email": "indah@example.com",
    "id": 4,
    "nama": "Indah",
    "phone": "0895",
    "username": "indah123",
    "loc": "Bandung, Jawa Barat",
    "avatar": "filename"
  }
}
```

**Edit Profile**
**Path :**

> /Profile

**Method :**

> `PUT`

**Header :**

> `Authorization` : `Bearer <token>` > `Content-Type`: `application/json`

**Request Body :**
add new data:

> - nama as `string`
> - phone as `string`
> - username as `string`, must be unique
> - password as `string`, must be at least 8 characters
> - loc as `string`

**Response :**

```json
{
  "message": "Profile updated successfully!",
  "profile": {
    "email": "newemail@example.com",
    "id": 4,
    "nama": "New Name",
    "phone": "new phone",
    "username": "new_username",
    "loc": "new_location"
  }
}
```

**Upload Avatar User**
**Path :**

> /avatar

**Method :**

> `POST`

**Header :**

> `Authorization` : `Bearer <token>` > `Content-Type`: `multipart/form-data`

**Request Body :**

> - `avatar` as `file 'png', 'jpg', 'jpeg', 'gif'`

**Response :**

```json
{
  "filename": "channels4_profile.jpg",
  "message": "Avatar uploaded successfully!"
}
```

**Errorr Response :**

```json
{
  "error": true,
  "message": "Invalid email or password!"
}
```

**Tampilan Avatar User**
**Path :**

> /avatar/{filename}

**Method :**

> `GET`

**Header :**

> `Authorization` : `Bearer <token>` > `Content-Type`: `multipart/form-data`

**Response :**

> avatar image
