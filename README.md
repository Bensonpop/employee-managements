# Employee Management REST API

A RESTful API for managing employee records, built with **Java 21**, **Spring Boot**, **Spring Data JPA**, and **MySQL**. The application supports employee CRUD operations, input validation, duplicate-email protection, pagination, search, and active-status filtering.

## Features

- Create an employee
- Retrieve all employees
- Retrieve an employee by ID
- Update an employee
- Delete an employee
- Search employees by name, email, or department
- Filter employees by active status
- Paginate search results
- Validate request data
- Handle errors with meaningful HTTP status codes
- Explore API documentation through Swagger UI (if enabled in the project)

## Technology Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA / Hibernate
- MySQL 8
- Maven Wrapper
- Bean Validation
- Springdoc OpenAPI / Swagger UI (if configured)

## Prerequisites

Install the following before running the application:

- JDK 21
- MySQL Server 8.x
- Git (optional, for version control)

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Database Setup

1. Start your MySQL server.
2. Create the database:

```sql
CREATE DATABASE employee_management;
```

The application uses this connection URL by default:

```text
jdbc:mysql://localhost:3306/employee_management
```

## Configuration

Open `src/main/resources/application.properties`. Configure the database username and password using environment variables:

```properties
spring.application.name=employee-management

spring.datasource.url=jdbc:mysql://localhost:3306/employee_management
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

server.port=8080
```

Set these environment variables on your machine:

- `DB_USERNAME` — your MySQL username (defaults to `root` if not set)
- `DB_PASSWORD` — your MySQL password

**Security:** Do not commit database passwords, `.env` files containing secrets, or other credentials to GitHub.

### Set environment variables in Windows PowerShell

For the current terminal session only, run the following commands, replacing the example password locally with your own. Do not share your password in source control or chat.

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_local_mysql_password"
```

Alternatively, configure these variables in the IntelliJ IDEA Run Configuration for `EmployeeManagementApplication`.

## Run the Application

Open a terminal in the project root directory (the directory containing `mvnw.cmd`) and run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

To compile and run tests:

```powershell
.\mvnw.cmd clean test
```

## API Endpoints

Base URL: `http://localhost:8080/api/employees`

| Method | Endpoint | Description | Success response |
|---|---|---|---|
| `POST` | `/api/employees` | Create an employee | `201 Created` |
| `GET` | `/api/employees` | Get all employees | `200 OK` |
| `GET` | `/api/employees/{id}` | Get an employee by ID | `200 OK` |
| `PUT` | `/api/employees/{id}` | Update an employee | `200 OK` |
| `DELETE` | `/api/employees/{id}` | Delete an employee | `204 No Content` |
| `GET` | `/api/employees/search` | Search, filter, and paginate employees | `200 OK` |

### Create an Employee

**Request**

```http
POST /api/employees
Content-Type: application/json
```

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 55000.00,
  "active": true
}
```

Example response (`201 Created`):

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 55000.00,
  "active": true
}
```

The ID and response format are illustrative; the generated ID depends on the database.

### Get All Employees

```http
GET /api/employees
```

### Get an Employee by ID

```http
GET /api/employees/1
```

### Update an Employee

```http
PUT /api/employees/1
Content-Type: application/json
```

```json
{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "department": "Finance",
  "salary": 60000.00,
  "active": true
}
```

### Delete an Employee

```http
DELETE /api/employees/1
```

A successful delete returns `204 No Content`.

## Search, Filtering, and Pagination

The search endpoint supports optional query parameters:

| Parameter | Description | Example |
|---|---|---|
| `search` | Searches first name, last name, email, and department | `search=John` |
| `active` | Filters by active status | `active=true` |
| `page` | Zero-based page number | `page=0` |
| `size` | Number of records per page (maximum 100) | `size=5` |

Examples:

```http
GET /api/employees/search?search=John
GET /api/employees/search?active=true
GET /api/employees/search?page=0&size=5
GET /api/employees/search?search=John&active=true&page=0&size=5
```

The search endpoint returns a Spring Data `Page` response, including the matching employee content and pagination metadata.

## Validation and Error Handling

The API uses request validation and centralized exception handling. Depending on the error, responses can include:

| HTTP status | Meaning |
|---|---|
| `400 Bad Request` | Request validation failed |
| `404 Not Found` | Employee ID does not exist |
| `409 Conflict` | An employee with the email already exists |
| `500 Internal Server Error` | Unexpected server-side error |

Example duplicate-email response:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "An employee with this email already exists",
  "timestamp": "2026-10-23T23:42:36.157078"
}
```

The timestamp is an example. Actual error messages and response fields depend on the error handler.

## API Documentation

If the Springdoc OpenAPI dependency is enabled, start the application and open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Project Structure

```text
src/
├── main/
│   ├── java/com/codenzic/employee_management/
│   │   ├── controller/   # REST endpoints
│   │   ├── dto/          # Request and response models
│   │   ├── entity/       # JPA entity
│   │   ├── exception/    # Custom exceptions and global handler
│   │   ├── repository/   # Database access
│   │   ├── service/      # Business logic
│   │   └── EmployeeManagementApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/             # Automated tests
```

## Testing with Postman

1. Start the application.
2. Create a request in Postman using one of the endpoints above.
3. Set `Content-Type: application/json` for POST and PUT requests.
4. Test successful CRUD operations and error cases, including duplicate emails, invalid input, and a nonexistent employee ID.
5. Test search, active-status filtering, and pagination.

## Version Control

Before pushing this project to GitHub, check that:

- Database credentials are not included in committed files.
- `.gitignore` excludes build output such as `target/` and local IDE files.
- The project builds successfully using `.\mvnw.cmd clean test`.

## Benson

Developed as an Employee Management REST API project using Spring Boot and MySQL.
