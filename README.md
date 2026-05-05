# Case Management System
A Spring Boot–based backend application for managing cases, notes, notifications, and user roles with JWT 
authentication and role-based access control.

## 📦 Tech Stack
- Java
- Spring Boot
- Spring Security (JWT-based authentication)
- Maven
- REST API
- Docker (Dockerfile included)
- JUnit (unit & integration tests)

---

## 🚀 Features

### Authentication & Authorization
- JWT-based authentication
- User registration & login
- Role-based access control
- Role request system
  
### Case Management
- Create, update, and manage cases
- Case status transitions with validation
- Case prioritization
- Case logs (history tracking)
  
### Notes
- Add and manage case notes
- Linked to individual cases
  
### Notifications
- Notification system for users
- CRUD operations for notifications

### Reporting
- Admin statistics
- Reporting endpoints

## 🧱 Project Structure
```
src/main/java/com/example/casemanagement/
│
├── config/        # Security, JWT, CORS, OpenAPI, exception handling
├── controller/    # REST controllers (API endpoints)
├── dto/           # Data Transfer Objects
├── exception/     # Custom exceptions
├── mapper/        # Entity <-> DTO mapping
├── model/         # JPA entities
├── repository/    # Spring Data repositories
├── service/       # Business logic
└── domain/        # Domain-specific logic (e.g. status transitions)
```
## 🔐 Security
JWT authentication handled via:
- JwtAuthFilter
- JwtService
Spring Security configuration in:
- SecurityConfig
Custom user details service:
- CustomUserDetailsService

## 🔄 Case Status Flow
Case status transitions are controlled via:
- CaseStatus
- CaseStatusTransition
- CaseStatusService
Invalid transitions throw:
- InvalidTransitionException

---
## ⚙️ Setup & Run
1. Clone repository
```bash
git clone https://github.com/amer1301/casemanagement.git
cd casemanagement
```
2. Build projekt
```bash
./mvnw clean install
```
3. Run application
```bash
./mvnw spring-boot:run
```
4. Run with Docker
```bash
docker build -t casemanagement .
docker run -p 8080:8080 casemanagement
```
## 🔧 Configuration
Main configuration file:
```
src/main/resources/application.properties
```
Test configuration:
```
src/test/resources/application-test.properties
```
## 🧪 Testing
### Run all tests
```bash
./mvnw test
```
### Test types included
- Unit tests (services, repositories)
- Controller tests
- Integration tests (security, full flows)

## 📡 API Overview

Controllers available:
- **AuthController** – login & registration
- **CaseController** – case management
- **CaseNoteController** – notes
- **NotificationController** – notifications
- **ReportController** – reporting/statistics
- **RoleRequestController** – role upgrade requests
--- 
