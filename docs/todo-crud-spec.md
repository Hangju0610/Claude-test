# TODO List CRUD Functional Specification

## 1. Overview

This document defines the functional requirements for a TODO List CRUD API server built with Spring Boot. The server provides both a REST API and a Thymeleaf-based web UI for managing TODO items.

## 2. Domain Model

### 2.1 Todo Entity

| Field       | Type          | Constraints                              | Description               |
|-------------|---------------|------------------------------------------|---------------------------|
| id          | Long          | PK, auto-generated                       | Unique identifier         |
| title       | String        | NotBlank, max 200 chars                  | Title of the todo item    |
| description | String        | Nullable, max 1000 chars                 | Optional description      |
| completed   | Boolean       | NotNull, default false                   | Completion status         |
| createdAt   | LocalDateTime | Auto-set on creation, not updatable      | Creation timestamp        |
| updatedAt   | LocalDateTime | Auto-set on creation and update          | Last modification time    |

## 3. REST API Endpoints

### 3.1 Create Todo
- **POST** `/api/todos`
- **Request Body**: `{ "title": "string", "description": "string" }`
- **Validation**: `title` must not be blank and max 200 chars
- **Response**: `201 Created` with created Todo JSON
- **Error**: `400 Bad Request` on validation failure

### 3.2 Get All Todos
- **GET** `/api/todos`
- **Response**: `200 OK` with array of Todo JSON
- **Note**: Returns empty array if no todos exist

### 3.3 Get Todo by ID
- **GET** `/api/todos/{id}`
- **Response**: `200 OK` with Todo JSON
- **Error**: `404 Not Found` if todo does not exist

### 3.4 Update Todo
- **PUT** `/api/todos/{id}`
- **Request Body**: `{ "title": "string", "description": "string", "completed": boolean }`
- **Validation**: `title` must not be blank and max 200 chars
- **Response**: `200 OK` with updated Todo JSON
- **Error**: `404 Not Found` if todo does not exist, `400 Bad Request` on validation failure

### 3.5 Delete Todo
- **DELETE** `/api/todos/{id}`
- **Response**: `204 No Content`
- **Error**: `404 Not Found` if todo does not exist

## 4. Web UI (Thymeleaf)

### 4.1 Todo List Page
- **GET** `/todos`
- Displays all todos in a list
- Each item shows title, completion status, and action buttons (edit, delete, toggle complete)
- Includes a form/link to create new todos

### 4.2 Create Todo Page
- **GET** `/todos/new`
- Form with title and description fields
- Submits POST to `/todos` (form action)

### 4.3 Edit Todo Page
- **GET** `/todos/{id}/edit`
- Pre-filled form with current todo data
- Submits POST to `/todos/{id}` (form action with method override)

### 4.4 Web Controller Actions
- **POST** `/todos` -- create todo, redirect to list
- **POST** `/todos/{id}` -- update todo, redirect to list
- **POST** `/todos/{id}/delete` -- delete todo, redirect to list
- **POST** `/todos/{id}/toggle` -- toggle completion, redirect to list

## 5. Error Handling

- Global exception handler returns structured error responses for REST API
- REST API errors return JSON: `{ "status": int, "message": "string", "timestamp": "datetime" }`
- Web UI displays error messages via Thymeleaf flash attributes
- `TodoNotFoundException` maps to HTTP 404
- Validation errors map to HTTP 400

## 6. Architecture

```
Controller Layer (REST + Web)
    |
Service Layer (Business Logic)
    |
Repository Layer (JPA)
    |
Entity Layer (Domain Model)
```

### 6.1 Package Structure
```
com.example.todo
  ├── TodoApplication.java
  ├── controller
  │   ├── TodoApiController.java      (REST)
  │   └── TodoWebController.java      (Thymeleaf)
  ├── dto
  │   ├── TodoRequest.java
  │   └── TodoResponse.java
  ├── entity
  │   └── Todo.java
  ├── exception
  │   ├── GlobalExceptionHandler.java
  │   └── TodoNotFoundException.java
  ├── repository
  │   └── TodoRepository.java
  └── service
      └── TodoService.java
```

## 7. Requirements Checklist

| ID   | Requirement                                         | Priority |
|------|-----------------------------------------------------|----------|
| R01  | Create a todo with title and optional description   | High     |
| R02  | Retrieve all todos                                  | High     |
| R03  | Retrieve a single todo by ID                        | High     |
| R04  | Update a todo (title, description, completed)       | High     |
| R05  | Delete a todo by ID                                 | High     |
| R06  | Validate title is not blank and max 200 chars       | High     |
| R07  | Return 404 for non-existent todo                    | High     |
| R08  | Return 400 for validation errors                    | High     |
| R09  | Auto-set createdAt and updatedAt timestamps         | Medium   |
| R10  | New todos default to completed=false                | Medium   |
| R11  | Thymeleaf list page shows all todos                 | Medium   |
| R12  | Thymeleaf create/edit forms with validation         | Medium   |
| R13  | Toggle todo completion from web UI                  | Medium   |
| R14  | Structured JSON error responses for REST API        | Medium   |
| R15  | Swagger/OpenAPI documentation                       | Low      |

Total: 15 requirements
