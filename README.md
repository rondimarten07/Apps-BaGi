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

> `Authorization` : `Bearer <token>` > `Content-Type`: `application/json`

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
