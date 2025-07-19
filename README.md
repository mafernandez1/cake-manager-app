# Spring Boot App to Manage Cakes

Cake Management System is a Spring Boot application that provides a RESTful API for managing cakes, users, and roles. It includes user authentication and authorization using JWT tokens, role-based access control, and integrates with PostgreSQL for data persistence.

## Features

- User registration and login
- JWT token-based authentication
- Role-based access control (USER, ADMIN)
- Password encryption using BCrypt
- PostgreSQL database (H2 for development)
- Docker containerization
- Testcontainers integration testing
- RESTful API endpoints

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6+
- Docker & Docker Compose

### Running with Docker (Recommended)

1. Clone the repository
2. Navigate to the project directory
3. Start the application with Docker:
    ```bash
   docker compose up --build -d
   ```
   OR
    ```bash
   docker-compose up --build -d
   ```

The application will start on `http://localhost:8080` with PostgreSQL database.

### Running Locally (Development)

For development with H2 database:
```bash
mvn spring-boot:run
```

### Running Tests

Tests use Testcontainers to spin up real PostgreSQL instances:

```bash
mvn test
```

## API Endpoints

### Public access

**GET** `/api/actuator/health`

### API Documentation (Swagger/OpenAPI)

This application provides interactive API documentation using Swagger UI, powered by OpenAPI 3.

- **Swagger UI:**  
  Once the application is running, access the API docs at:  
  [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)  
  or  
  [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)

- **OpenAPI Spec:**  
  The raw OpenAPI JSON is available at:  
  [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)

Swagger UI allows you to explore and test all available endpoints, view request/response schemas, and see example payloads.

> **Note:**  
> The API is secured with JWT authentication. For protected endpoints, use the `Authorize` button in Swagger UI and provide a valid Bearer token.

### Authentication Endpoints

#### Register a new user

**POST** `/api/auth/sign-up`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "yourPassword",
  "roles": ["user"] // Optional, can be ["admin"] or omitted for default "user"
}
```

**Responses:**
```json
{
  "message": "User registered successfully"
}
```
```json
{
  "message": "Email is already in use"
}
```

#### Login a user
**POST** `/api/auth/sign-in`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "yourPassword"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "id": 1,
  "email": "test@example.com",
  "roles": ["ROLE_USER"]
}
```

**Note:** When running the application for the first time, you can use the default credentials to log in:
```json
{
  "email": "admin.demo@test.com",
  "password": "admin123"
}
```

### Protected Endpoints

Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Cake Management Endpoints

All endpoints require authentication (JWT Bearer token in the `Authorization` header), unless otherwise specified.

#### Get all cakes

**GET** `/cake`

**Response:**
- `200 OK`  
  Returns a JSON array of all cakes.

#### Get cake by ID

**GET** `/cake/{id}`

**Path Parameters:**
- `id` (Long): Cake ID

**Response:**
- `200 OK`  
  Returns the cake object.
- `404 Not Found`  
  If the cake does not exist.

#### Create a new cake

**POST** `/cake`

**Request Body:**
```json
{
  "name": "Chocolate Cake",
  "description": "Rich chocolate flavor",
  "imageUrl": "http://example.com/cake.jpg"
}
```

**Response:**

- `200 OK`  
  Returns the created cake object.
- `400 Bad Request`  
  If the request body is invalid.

#### Update an existing cake

**PUT** `/cake/{id}`

**Request Body:**
```json
{
  "title": "Updated Cake Name",
  "description": "Updated description",
  "imageUrl": "http://example.com/updated-cake.jpg"
}
```

**Response:**
- `200 OK`  
  Returns the updated cake object.
- `404 Not Found`  
  If the cake does not exist.
- `400 Bad Request`  
  If the request body is invalid.

#### Delete a cake

**DELETE** `/cake/{id}`

**Response:**

- `204 No Content`  
  If the cake was successfully deleted.
- `404 Not Found`  
  If the cake does not exist.


