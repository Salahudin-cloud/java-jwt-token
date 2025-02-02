# API SPEC

##  Auth Spec
### 1. Register 
- Method : POST 
- Endpoint : /api/v1/auth/register
- Request Body : 
```json
{
  "username" : "user_test", 
  "password" : "test"
}
```
- Response Body (Success) :
```json
{
  "message" : "OK"
}
```
- Response Body (Failed) example :
```json
{
  "message": "username: must not be blank"
}
```
### 2. Login
- Method : POST
- Endpoint : /api/v1/auth/login
- Request Body :
```json
{
  "username" : "user_test", 
  "password" : "test"
}
```
- Response Body (Success) :
```json
{
  "token" : "some_token"
}
```
- Response Body (Failed) example :
```json
{
  "message": "Invalid username or password"
}
```
## User
### 1. add user
- Method : POST
- Endpoint : /api/v1/users
- Role: admin
- Headers :
```markdown
Authorization: Bearer <your_token_here>
```
- Request Body :
```json
{
  "username" : "user_test", 
  "password" : "test"
}
```
- Response Body (Success) :
```json
{
  "message" : "OK"
}
```
- Response Body (Failed) example :
```json
{
  "message": "username: must not be blank"
}
```
### 2. update user
- Method : PUT
- Endpoint : /api/v1/users/{id}
- Role: admin
- Headers :
```markdown
Authorization: Bearer <your_token_here>
```
- Request Body :
```json
{
  "username" : "user_test", 
  "password" : "test"
}
```
- Response Body (Success) :
```json
{
  "message": "OK",
  "data": {
    "id": 303,
    "username": "udin",
    "password": "$2a$15$r/7MaXRljr3oYWM3rAiD7OvoVQGczorbCK6lF5DVag0vrXGTblf6y",
    "role": "user",
    "created_at": "2025-01-19T16:03:23.385+00:00",
    "update_at": "2025-01-20T02:51:24.784+00:00",
    "currentPage": null,
    "itemPerPage": null
  }
}
```
- Response Body (Failed) example :
```json
{
  "message": "role: must not be blank"
}
```
### 3. delete user
- Method : DELETE
- Endpoint : /api/v1/users/{id}
- Role: admin
- Headers :
```markdown
Authorization: Bearer <your_token_here>
```
- Response Body (Success) :
```json
{
  "message": "OK"
}
```
### 4. List user
- Method : GET
- Endpoint : /api/v1/users
- Role: admin
- Headers :
```markdown
Authorization: Bearer <your_token_here>
```
- Response Body (Success) :
```json
{
  "message": "OK",
  "data": [
    {
      "id": 302,
      "username": "user_test",
      "password": "$2a$15$lTmlmTiHo1i0O62biyg2z.XmjbfWVbHXxC.7Ub4SZs01mII/8ysha",
      "role": "admin",
      "created_at": "2025-01-19T15:28:15.461+00:00",
      "update_at": null,
      "currentPage": 0,
      "itemPerPage": 1
    }
  ]
}
```
