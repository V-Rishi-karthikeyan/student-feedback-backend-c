# Student Feedback System - Backend

A Spring Boot REST API backend for managing student feedback on courses.

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security + JWT**
- **MySQL**
- **Maven**
- **Lombok**

---

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Database Setup
```sql
CREATE DATABASE feedback_system;
```

### Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### Run
```bash
mvn spring-boot:run
```

---

## API Endpoints

### Auth
| Method | Endpoint              | Description         | Access  |
|--------|-----------------------|---------------------|---------|
| POST   | /api/auth/register    | Register new user   | Public  |
| POST   | /api/auth/login       | Login & get token   | Public  |

### Feedback
| Method | Endpoint                        | Description                     | Access          |
|--------|---------------------------------|---------------------------------|-----------------|
| POST   | /api/feedback/submit            | Submit feedback                 | STUDENT         |
| GET    | /api/feedback/my                | View my feedback                | STUDENT         |
| GET    | /api/feedback/course/{courseId} | View feedback for a course      | TEACHER, ADMIN  |
| GET    | /api/feedback/teacher/my-courses| View all feedback for teacher   | TEACHER         |
| GET    | /api/feedback/all               | View all feedback               | ADMIN           |
| DELETE | /api/feedback/{id}              | Delete a feedback entry         | ADMIN           |

### Users
| Method | Endpoint              | Description         | Access  |
|--------|-----------------------|---------------------|---------|
| GET    | /api/users/me         | Get current user    | All     |
| GET    | /api/users            | Get all users       | ADMIN   |
| GET    | /api/users/{id}       | Get user by ID      | ADMIN   |
| GET    | /api/users/role/{role}| Get users by role   | ADMIN   |
| PUT    | /api/users/{id}       | Update user         | ADMIN   |
| DELETE | /api/users/{id}       | Delete user         | ADMIN   |

### Reports
| Method | Endpoint                        | Description              | Access          |
|--------|---------------------------------|--------------------------|-----------------|
| GET    | /api/reports/course/{courseId}  | Course report            | TEACHER, ADMIN  |
| GET    | /api/reports/teacher/my-report  | My courses report        | TEACHER         |
| GET    | /api/reports/teacher/{teacherId}| Teacher report (by ID)   | ADMIN           |
| GET    | /api/reports/admin/summary      | System summary report    | ADMIN           |

---

## Authentication

All protected endpoints require a Bearer token in the header:
```
Authorization: Bearer <token>
```

### Sample Login Request
```json
POST /api/auth/login
{
  "email": "admin@feedback.com",
  "password": "admin123"
}
```

### Sample Register Request
```json
POST /api/auth/register
{
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "password": "password123",
  "role": "STUDENT"
}
```

### Sample Feedback Submission
```json
POST /api/feedback/submit
Authorization: Bearer <student_token>
{
  "courseId": 1,
  "rating": 5,
  "comments": "Excellent course! Very well explained."
}
```

---

## Default Seed Users (from data.sql)

| Role    | Email                     | Password   |
|---------|---------------------------|------------|
| ADMIN   | admin@feedback.com        | admin123   |
| TEACHER | john.smith@feedback.com   | admin123   |
| TEACHER | jane.doe@feedback.com     | admin123   |
| STUDENT | alice@feedback.com        | admin123   |
| STUDENT | bob@feedback.com          | admin123   |

---

## Project Structure

```
com.feedbacksystem
├── FeedbackSystemApplication.java
├── controller/
│   ├── AuthController.java
│   ├── FeedbackController.java
│   ├── UserController.java
│   └── ReportController.java
├── service/
│   ├── AuthService.java
│   ├── FeedbackService.java
│   ├── UserService.java
│   ├── ReportService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── FeedbackServiceImpl.java
│       ├── UserServiceImpl.java
│       └── ReportServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   ├── CourseRepository.java
│   └── FeedbackRepository.java
├── entity/
│   ├── User.java
│   ├── Course.java
│   ├── Feedback.java
│   └── Role.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── FeedbackRequest.java
│   ├── FeedbackResponse.java
│   └── AuthResponse.java
├── config/
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   └── JwtAuthenticationFilter.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
└── util/
    └── JwtUtil.java
```
