# JobTracker

A full-stack job application tracker built with Spring Boot, Thymeleaf, and PostgreSQL/H2.
Track every application from wishlist to offer — with a clean REST API alongside the server-rendered UI.

---

## Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Backend    | Spring Boot 3.2, Java 21            |
| Persistence| Spring Data JPA / Hibernate         |
| Database   | PostgreSQL (prod), H2 in-memory (dev)|
| Frontend   | Thymeleaf + plain CSS/JS            |
| Validation | Jakarta Validation (Bean Validation) |
| Testing    | JUnit 5, Mockito, MockMvc           |

---

## Getting Started

### Run with H2 (no setup required)

```bash
./mvnw spring-boot:run
```

App starts at `http://localhost:8080`.  
H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:jobtrackerdb`).

### Run with PostgreSQL

1. Create a database:
   ```sql
   CREATE DATABASE jobtrackerdb;
   ```

2. Run with the postgres profile:
   ```bash
   DB_USERNAME=postgres DB_PASSWORD=yourpassword \
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
   ```

---

## REST API

All endpoints are under `/api/applications`.

| Method | Endpoint                   | Description                  |
|--------|----------------------------|------------------------------|
| GET    | `/api/applications`        | List all (filterable by status/search) |
| GET    | `/api/applications/{id}`   | Get single application       |
| POST   | `/api/applications`        | Create new application       |
| PUT    | `/api/applications/{id}`   | Update application           |
| DELETE | `/api/applications/{id}`   | Delete application           |
| GET    | `/api/applications/stats`  | Aggregate stats              |

**Query params for GET `/api/applications`:**
- `?status=INTERVIEW` — filter by status
- `?search=zalando` — search company or role

**Application statuses:** `WISHLIST`, `APPLIED`, `PHONE_SCREEN`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`

---

## Project Structure

```
src/main/java/com/hammad/jobtracker/
├── controller/
│   ├── JobApplicationApiController.java   # REST API
│   └── JobApplicationWebController.java   # Thymeleaf MVC
├── service/
│   └── JobApplicationService.java
├── repository/
│   └── JobApplicationRepository.java
├── model/
│   ├── JobApplication.java
│   └── ApplicationStatus.java
├── dto/
│   ├── JobApplicationDto.java
│   └── StatsDto.java
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
└── JobTrackerApplication.java
```

---

## Running Tests

```bash
./mvnw test
```

---

## Sample API Request

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Zalando",
    "role": "Backend Engineer",
    "location": "Berlin",
    "status": "APPLIED",
    "appliedDate": "2026-03-29",
    "notes": "Applied via careers page"
  }'
```
# JobTracker
