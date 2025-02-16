-- Drop the users_db database if it exists
DROP DATABASE IF EXISTS users_db;

-- Create the clients_db database
CREATE DATABASE users_db;

-- Use the clients_db database
USE users_db;

-- Drop the authorities table if it exists
DROP TABLE IF EXISTS authorities;
-- Drop the roles table if it exists
DROP TABLE IF EXISTS roles;
-- Drop the roles_authorities table if it exists
DROP TABLE IF EXISTS roles_authorities;
-- Drop the users table if it exists
DROP TABLE IF EXISTS users;
-- Drop the users_roles table if it exists
DROP TABLE IF EXISTS users_roles;

-- Create Authorities Table
CREATE TABLE IF NOT EXISTS authorities
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create Roles Table
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create Role-Authority Relationship Table
CREATE TABLE IF NOT EXISTS roles_authorities
(
    role_id      BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS users
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            VARCHAR(100) NOT NULL UNIQUE,
    first_name         VARCHAR(50)  NOT NULL,
    last_name          VARCHAR(50)  NOT NULL,
    email              VARCHAR(100) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255) NOT NULL
);

-- Create User-Role Relationship Table
CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Insert Default Authorities
INSERT INTO authorities (name)
VALUES ('READ'),
       ('WRITE'),
       ('DELETE')
ON DUPLICATE KEY UPDATE name = name;

-- Insert Default Roles
INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name = name;

-- Assign Authorities to Roles
INSERT INTO roles_authorities (role_id, authority_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_USER'), id
FROM authorities
WHERE name IN ('READ', 'WRITE')
ON DUPLICATE KEY UPDATE role_id=role_id;

INSERT INTO roles_authorities (role_id, authority_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'), id
FROM authorities
ON DUPLICATE KEY UPDATE role_id=role_id;

-- Insert Default Admin User
INSERT INTO users (user_id, first_name, last_name, email, encrypted_password)
VALUES (UUID(), 'Jack', 'Sparrow', 'jack.sparrow@gmail.com',
        '$2a$10$KIX/5FT.S0u1fN9tbxg7QOqFf62.kXdu3GDFgfLX4Za15Qv0qP5XW'),
       (UUID(), 'Jack', 'Sparrow', 'jack.sparrow2@gmail.com',
        '$2a$10$KIX/5FT.S0u1fN9tbxg7QOqFf62.kXdu3GDFgfLX4Za15Qv0qP5XW')
ON DUPLICATE KEY UPDATE email=email;

-- Assign ROLE_ADMIN to Jack Sparrow Users
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email IN ('jack.sparrow@gmail.com', 'jack.sparrow2@gmail.com')
ON DUPLICATE KEY UPDATE user_id = u.id;