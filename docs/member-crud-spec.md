# Member CRUD Functional Specification

## 1. Overview

This document defines the functional requirements for Member management and member-based TODO list access. The feature provides member registration (sign-up) and the ability to view TODO items per member. Authentication (login/logout, JWT, session, Spring Security) is explicitly out of scope -- member identification is handled via `memberId` passed as a path variable or request parameter.

## 2. Domain Model

### 2.1 Member Entity

| Field     | Type          | Constraints                         | Description             |
|-----------|---------------|-------------------------------------|-------------------------|
| id        | Long          | PK, auto-generated                  | Unique identifier       |
| username  | String        | NotBlank, max 50 chars, unique      | Display name            |
| email     | String        | NotBlank, max 100 chars, unique, valid email format | Email address |
| createdAt | LocalDateTime | Auto-set on creation, not updatable | Registration timestamp  |

### 2.2 Todo Entity (Modified)

| Field    | Type   | Constraints                          | Description                       |
|----------|--------|--------------------------------------|-----------------------------------|
| member   | Member | Nullable, ManyToOne, FK to Member.id | Owner of the todo item            |

- The `member` field is nullable to maintain backward compatibility with existing TODO items that have no owner.
- When a member is deleted, their TODO items should have the `member` field set to null (no cascade delete on todos).

### 2.3 Entity Relationships

```
Member (1) ----< (0..*) Todo
  - One member can own many todos
  - A todo may or may not have an owner (nullable)
```

## 3. REST API Endpoints

### 3.1 Register Member (Sign Up)
- **POST** `/api/members`
- **Request Body**: `{ "username": "string", "email": "string" }`
- **Validation**:
  - `username`: NotBlank, max 50 chars
  - `email`: NotBlank, max 100 chars, valid email format
- **Response**: `201 Created` with created Member JSON
- **Errors**:
  - `400 Bad Request` on validation failure
  - `409 Conflict` if username or email already exists

### 3.2 Get Member by ID
- **GET** `/api/members/{id}`
- **Response**: `200 OK` with Member JSON
- **Error**: `404 Not Found` if member does not exist

### 3.3 Get All Members
- **GET** `/api/members`
- **Response**: `200 OK` with array of Member JSON
- **Note**: Returns empty array if no members exist

### 3.4 Get Todos by Member
- **GET** `/api/members/{memberId}/todos`
- **Response**: `200 OK` with array of Todo JSON belonging to the member
- **Error**: `404 Not Found` if member does not exist
- **Note**: Returns empty array if member exists but has no todos

### 3.5 Create Todo for Member
- **POST** `/api/members/{memberId}/todos`
- **Request Body**: `{ "title": "string", "description": "string" }`
- **Validation**: Same as existing todo creation (title NotBlank, max 200 chars)
- **Response**: `201 Created` with created Todo JSON (member association set)
- **Errors**:
  - `400 Bad Request` on validation failure
  - `404 Not Found` if member does not exist

## 4. Web UI (Thymeleaf)

### 4.1 Member Registration Page
- **GET** `/members/new`
- Form with username and email fields
- Submits POST to `/members` (form action)
- On success, redirect to member list page
- On validation error, redisplay form with error messages

### 4.2 Member List Page
- **GET** `/members`
- Displays all registered members
- Each member shows username, email, and creation date
- Link to view member's TODO list

### 4.3 Member Todo List Page
- **GET** `/members/{memberId}/todos`
- Displays all TODO items belonging to the specific member
- Shows member info header (username, email)
- Includes link to create new TODO for this member
- Reuses existing TODO item display (title, description, completed status, action buttons)

### 4.4 Web Controller Actions
- **POST** `/members` -- register member, redirect to member list
- **GET** `/members` -- member list page
- **GET** `/members/new` -- registration form
- **GET** `/members/{memberId}/todos` -- member's todo list page
- **POST** `/members/{memberId}/todos` -- create todo for member, redirect to member's todo list
- **POST** `/members/{memberId}/todos/{todoId}/toggle` -- toggle todo completion, redirect to member's todo list
- **POST** `/members/{memberId}/todos/{todoId}/delete` -- delete todo, redirect to member's todo list

## 5. Error Handling

- `MemberNotFoundException` maps to HTTP 404 (same pattern as TodoNotFoundException)
- `DuplicateMemberException` maps to HTTP 409 Conflict (username or email already taken)
- REST API errors return structured JSON: `{ "status": int, "message": "string", "timestamp": "datetime" }`
- GlobalExceptionHandler is extended to cover MemberApiController
- Web UI displays error messages via Thymeleaf model attributes

## 6. Architecture

### 6.1 New Package Structure

```
com.example.todo
  ├── controller
  │   ├── MemberApiController.java      (REST)
  │   └── MemberWebController.java      (Thymeleaf)
  ├── dto
  │   ├── MemberRequest.java
  │   └── MemberResponse.java
  ├── entity
  │   ├── Member.java
  │   └── Todo.java                     (modified: add member field)
  ├── exception
  │   ├── DuplicateMemberException.java
  │   ├── GlobalExceptionHandler.java   (extended)
  │   └── MemberNotFoundException.java
  ├── repository
  │   ├── MemberRepository.java
  │   └── TodoRepository.java           (modified: add findByMemberId)
  └── service
      ├── MemberService.java
      └── TodoService.java              (modified: add member-aware methods)
```

### 6.2 Existing API Backward Compatibility

- All existing `/api/todos` endpoints continue to work unchanged
- Existing todos without a member association remain accessible via existing endpoints
- The Todo entity's `member` field is nullable, preserving backward compatibility

## 7. Out of Scope

- Authentication (login, logout, JWT, session management)
- Authorization (access control, role-based permissions)
- Spring Security integration
- Password storage or management
- Member profile update or deletion (future feature)

## 8. Requirements Checklist

| ID   | Requirement                                                | Priority |
|------|------------------------------------------------------------|----------|
| M01  | Register a member with username and email                  | High     |
| M02  | Validate username: NotBlank, max 50, unique                | High     |
| M03  | Validate email: NotBlank, max 100, valid format, unique    | High     |
| M04  | Return 409 Conflict on duplicate username or email         | High     |
| M05  | Retrieve a member by ID                                    | High     |
| M06  | Return 404 for non-existent member                         | High     |
| M07  | Retrieve all members                                       | High     |
| M08  | Retrieve todos belonging to a specific member              | High     |
| M09  | Create a todo associated with a member                     | High     |
| M10  | Todo.member field is nullable (backward compatible)        | High     |
| M11  | Auto-set member createdAt timestamp                        | Medium   |
| M12  | Thymeleaf member registration page with validation         | Medium   |
| M13  | Thymeleaf member list page                                 | Medium   |
| M14  | Thymeleaf member todo list page                            | Medium   |
| M15  | Toggle and delete todos from member todo list page         | Medium   |
| M16  | Existing /api/todos endpoints remain backward compatible   | High     |
| M17  | Structured JSON error responses for member REST API        | Medium   |
| M18  | Swagger/OpenAPI documentation for member endpoints         | Low      |
| M19  | TodoRepository.findByMemberId query method                 | High     |

Total: 19 requirements
