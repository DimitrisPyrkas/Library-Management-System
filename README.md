# 📚 Library Management System

This is a Spring Boot Java project for managing a library. It supports user roles (ADMIN, MEMBER), book borrowing logic, JWT-based authentication, and more.

## 🛠️ Tech Stack

- Java 17+
- Spring Boot 3.5
- Spring Data JPA
- Spring Security + JWT
- MySQL 8 + JPA/Hibernate
- IntelliJ IDEA

****

## ✅ Features

- 🔐 Login & JWT Authentication for secure access
- 👥 User Roles: Admin and Member
- 📘 Book Management: Add, update, delete, borrow, return books
- 📄 Transaction tracking
- 📅 Borrowing rules (max copies, limits, durations)
- 🔑 Change password (securely)
- 🔒 Environment variable-based secrets for security


****

## 🧪 Testing the API
1. **Start the application** using IntelliJ.
2. Open **Postman** and test endpoints:
    - `POST /api/auth/register` → Register a new user.
    - `POST /api/auth/login` → Login and get JWT token.
    - `GET /api/users` → List users (requires ADMIN role and token).
3. **Add JWT token** in the Authorization tab

****

## 🛡️ Security
- JWT secret and database credentials are not hardcoded.
- Secrets are passed via environment variables
- Example setup: see `.env.example`

****

## 🔐 Setting the JWT Secret Key
  ✅ Step 1: Generate a secret key
  - Use a secure, random key generator. Here are a few ways:
    - Linux/macOS (Command Line) 
    <pre> ```openssl rand -base64 32 ``` </pre>
    - Open source libraries for generating a secret key

  ✅ Step 2: Set the secret key as an environment variable
  - 💻 Linux/macOS
    - Add this to your terminal or .bashrc / .zshrc file:
    <pre> ``` export JWT_SECRET="your-random-key" ``` </pre>
 - 🪟 Windows (Command Prompt or inside IDE)
   - <pre> ``` setx JWT_SECRET "your-random-key" ``` </pre>
   - Pass the secret key in environment variables .

  