# SQL Documentation for Java JWT token

### 1. Database Overview
- nama database : token
- Tujuan database : untuk mennyimpan data pengguna
- Jumlah tabel : 1 

### 2. Tabel Dalam Database
#### Tabel : `users`

| Kolom      | Tipe Data     | Constraint                      | Deskripsi                |
|------------|---------------|----------------------------------|--------------------------|
| id         | INT           | PRIMARY KEY, AUTO_INCREMENT     | ID unik untuk pengguna   |
| username   | VARCHAR(50)   | UNIQUE, NOT NULL                | Username pengguna        |
| password   | VARCHAR(255)  | NOT NULL                        | Password (hashed)        |
| role       | VARCHAR(255)  | NOT NULL                        | Role pengguna            |
| created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP       | Waktu pembuatan akun     |
| updated_at | TIMESTAMP     | NULL                            | Waktu update akun        |

### 3. Query SQL 
* Table user
1. Menambahkan data 
- Endpoint : ```/api/v1/users```
- Method : POST  
- Sql query : 
```postgres-sql
INSERT INTO users (username, password, role, created_at, updated_at) 
VALUES ('John', 'hashed_password', 'user', 'current timestamp', null );
```
2. Mengupdate data (ex : update username)
- Endpoint: ```/api/v1/users/{id}```
- Method : PUT
- Sql query : 
```postgres-sql
UPDATE users SET username = 'doe' WHERE id = 1;
```
3. Menghapus data
- Endpoint: ```/api/v1/users/{id}```
- Method : DELETE
- Sql Query : 
```postgres-sql
DELETE FROM users WHERE id = 1; 
```
4. Mengambil semua data pengguna
- Endpoint: ```/api/v1/users```
- Method : GET
- Sql query :
```postgres-sql
SELECT * FROM users;
```
5. Mendapatkan data pengguna berdasarkan id
- Endpoint : ```/api/v1/users/{id}```
- Method : GET
- Sql query : 
```postgres-sql
SELECT * FROM users WHERE id = 1; 
```
