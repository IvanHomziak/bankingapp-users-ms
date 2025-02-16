-- Drop the existing database if it exists
DROP DATABASE IF EXISTS users_db;

-- Create a new database
CREATE DATABASE users_db;

-- Use the new database
USE users_db;

-- Drop tables in correct order to avoid foreign key issues
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS roles_authorities;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS authorities;

-- Create Authorities Table
CREATE TABLE IF NOT EXISTS authorities (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(50) NOT NULL UNIQUE
);

-- Create Roles Table
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id VARCHAR(100) NOT NULL UNIQUE,
                                     first_name VARCHAR(50) NOT NULL,
                                     last_name VARCHAR(50) NOT NULL,
                                     email VARCHAR(120) NOT NULL UNIQUE,
                                     encrypted_password VARCHAR(255) NOT NULL UNIQUE
);

-- Create Role-Authority Relationship Table
CREATE TABLE IF NOT EXISTS roles_authorities (
                                                 roles_id BIGINT NOT NULL,
                                                 authorities_id BIGINT NOT NULL,
                                                 PRIMARY KEY (roles_id, authorities_id),
                                                 FOREIGN KEY (roles_id) REFERENCES roles(id) ON DELETE CASCADE,
                                                 FOREIGN KEY (authorities_id) REFERENCES authorities(id) ON DELETE CASCADE
);

-- Create User-Role Relationship Table
CREATE TABLE IF NOT EXISTS users_roles (
                                           users_id BIGINT NOT NULL,
                                           roles_id BIGINT NOT NULL,
                                           PRIMARY KEY (users_id, roles_id),
                                           FOREIGN KEY (users_id) REFERENCES users(id) ON DELETE CASCADE,
                                           FOREIGN KEY (roles_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert Default Authorities
INSERT INTO authorities (name)
VALUES ('READ'), ('WRITE'), ('DELETE')
ON DUPLICATE KEY UPDATE name = name;

-- Insert Default Roles
INSERT INTO roles (name)
VALUES ('ROLE_USER'), ('ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name = name;

-- Assign Authorities to Roles
INSERT INTO roles_authorities (roles_id, authorities_id)
SELECT r.id, a.id
FROM roles r
         JOIN authorities a ON a.name IN ('READ', 'WRITE')
WHERE r.name = 'ROLE_USER'
ON DUPLICATE KEY UPDATE roles_id=roles_id;

INSERT INTO roles_authorities (roles_id, authorities_id)
SELECT r.id, a.id
FROM roles r
         JOIN authorities a ON 1=1
WHERE r.name = 'ROLE_ADMIN'
ON DUPLICATE KEY UPDATE roles_id=roles_id;

-- Insert Default Users
INSERT INTO users (user_id, first_name, last_name, email, encrypted_password)
VALUES
    ('uuid-1', 'Jack', 'Sparrow', 'jack.sparrow@gmail.com',
     '$2a$10$9js9U4lhw6nC/rqNXqUw6.fILc2lty5q.oMUaocA4bCGh2SGl14Pi'),
    ('uuid-2', 'Jack', 'Sparrow', 'jack.sparrow2@gmail.com',
     '$2a$10$9js9U4lhw6nC/rqNXqUw6.fILc2lty5q.oMUaocA4bCGh2SGl14Pi')
ON DUPLICATE KEY UPDATE email=email;

-- Assign ROLE_ADMIN to Jack Sparrow Users
INSERT INTO users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email IN ('jack.sparrow@gmail.com', 'jack.sparrow2@gmail.com')
ON DUPLICATE KEY UPDATE users_id = u.id;
