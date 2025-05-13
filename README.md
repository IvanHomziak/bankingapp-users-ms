# BankingApp - Users Microservice

This microservice handles all user-related operations in the BankingApp system, including:

- User registration and authentication
- OAuth2 login (GitHub/Google)
- JWT-based authentication
- Two-factor authentication (2FA) via Google Authenticator
- Role-based access control (RBAC)
- Admin functionalities to manage user accounts

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security (JWT & OAuth2)
- JPA (Hibernate)
- MySQL / PostgreSQL
- Redis (optional)
- Google Authenticator for 2FA
- Maven
- Lombok
- Eureka (Spring Cloud Service Discovery)
- Docker

---

## 📁 Project Structure

```
com.ihomziak.bankingapp.api.users
├── controller/       # REST API endpoints for authentication, admin, and audit logs
├── entity/           # JPA entity definitions (User, Role, AuditLog, etc.)
├── dto/              # Data Transfer Objects
├── repository/       # Spring Data JPA repositories
├── security/         # JWT filters, OAuth2 handlers, authentication services
├── service/          # Business logic and service interfaces
├── util/             # Utilities (email service, auth helpers)
└── config/           # OAuth2 success handler and security config
```

---

## 🚀 Getting Started

### 1. Configure Application Properties

Add the following to your `application.properties` or `application.yml`:

```properties
# JWT configuration
spring.app.jwtSecret=BASE64_SECRET_KEY
spring.app.jwtExpirationMs=86400000

# OAuth2 configuration
spring.security.oauth2.client.registration.github.client-id=...
spring.security.oauth2.client.registration.github.client-secret=...
spring.security.oauth2.client.registration.google.client-id=...
spring.security.oauth2.client.registration.google.client-secret=...

# Frontend URL
frontend.url=http://localhost:4200

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/banking_users
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 2. Run the Project

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🔐 Authentication Features

- Email/password login
- OAuth2 login (GitHub, Google)
- JWT-based token authentication
- Two-Factor Authentication (2FA)
- Password reset via email
- Role-based permissions (Admin, User, etc.)

---

## 📦 Admin Endpoints

| Method | Endpoint                            | Description                       |
|--------|-------------------------------------|-----------------------------------|
| GET    | `/api/admin/getusers`              | Fetch all users                   |
| PUT    | `/api/admin/update-role`           | Update user role                  |
| PUT    | `/api/admin/update-lock-status`    | Lock or unlock user accounts      |
| PUT    | `/api/admin/update-enabled-status` | Enable or disable user accounts   |
| PUT    | `/api/admin/update-password`       | Change user password              |

---

## 🔑 Auth Endpoints

| Method | Endpoint                            | Description                       |
|--------|-------------------------------------|-----------------------------------|
| POST   | `/api/auth/public/signup`          | Register a new user               |
| POST   | `/api/auth/public/signin`          | Login with credentials            |
| GET    | `/api/auth/user`                   | Get current user info             |
| POST   | `/api/auth/public/forgot-password` | Request password reset            |
| POST   | `/api/auth/public/reset-password`  | Perform password reset            |
| POST   | `/api/auth/enable-2fa`             | Enable two-factor auth            |
| POST   | `/api/auth/verify-2fa`             | Verify 2FA code                   |
| GET    | `/api/auth/user/2fa-status`        | Check 2FA status                  |

---

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

---

## 📜 License

This project is licensed under the MIT License.