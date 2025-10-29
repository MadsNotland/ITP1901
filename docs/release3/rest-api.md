# Workout Tracker REST API Documentation

## Overview

This document describes the REST API endpoints for the Workout Tracker application. The API provides CRUD (Create, Read, Update, Delete) operations for managing workout sessions.

## Base URL

```
http://localhost:8080/api
```

## Conventions

- All endpoints return JSON responses
- CORS is enabled for all origins (`*`)
- All timestamps use ISO 8601 format (YYYY-MM-DD)
- Time values are in hours (decimal format)
- IDs are automatically generated as sequential integers

## Data Model

### Workout Object

```json
{
  "id": 1,
  "type": "Running",
  "notes": "Morning jog in the park",
  "date": "2025-10-25",
  "time": 1.5
}
```

**Fields:**
- `id` (Long): Unique identifier, auto-generated
- `type` (String): Type of workout (e.g., "Running", "Gym", "Swimming")
- `notes` (String): Additional notes about the workout
- `date` (String): Date of the workout in YYYY-MM-DD format
- `time` (Number): Duration in hours (decimal format)

## Endpoints

### 1. Get All Workouts

**GET** `/api/workouts`

Retrieves all workout sessions.

**Response:**
- **200 OK**: List of all workouts

**Example Response:**
```json
[
  {
    "id": 1,
    "type": "Running",
    "notes": "Morning jog",
    "date": "2025-10-25",
    "time": 1.5
  },
  {
    "id": 2,
    "type": "Gym",
    "notes": "Strength training",
    "date": "2025-10-24",
    "time": 2.0
  }
]
```

**Empty Response:**
```json
[]
```

---

### 2. Create New Workout

**POST** `/api/workouts`

Creates a new workout session. The ID will be automatically assigned.

**Request Body:**
```json
{
  "type": "Running",
  "notes": "Morning jog in the park",
  "date": "2025-10-25",
  "time": 1.5
}
```

**Response:**
- **201 Created**: The created workout with assigned ID

**Example Response:**
```json
{
  "id": 1,
  "type": "Running",
  "notes": "Morning jog in the park",
  "date": "2025-10-25",
  "time": 1.5
}
```

**Validation Rules:**
- `type`: Required, cannot be null or empty
- `notes`: Required, cannot be null or empty
- `date`: Required, cannot be null
- `time`: Required, must be greater than 0

---

### 3. Get Workout by ID

**GET** `/api/workouts/{id}`

Retrieves a specific workout by its ID.

**Path Parameters:**
- `id` (Long): The workout ID

**Response:**
- **200 OK**: The requested workout
- **404 Not Found**: Workout not found

**Example Response (200 OK):**
```json
{
  "id": 1,
  "type": "Running",
  "notes": "Morning jog in the park",
  "date": "2025-10-25",
  "time": 1.5
}
```

**Example Response (404 Not Found):**
```json
{
  "timestamp": "2025-10-25T19:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/workouts/999"
}
```

---

### 4. Update Workout

**PUT** `/api/workouts/{id}`

Updates an existing workout. The ID in the path must match the workout being updated.

**Path Parameters:**
- `id` (Long): The workout ID to update

**Request Body:**
```json
{
  "type": "Swimming",
  "notes": "Pool session with intervals",
  "date": "2025-10-25",
  "time": 2.0
}
```

**Response:**
- **200 OK**: The updated workout
- **404 Not Found**: Workout not found

**Example Response (200 OK):**
```json
{
  "id": 1,
  "type": "Swimming",
  "notes": "Pool session with intervals",
  "date": "2025-10-25",
  "time": 2.0
}
```

**Example Response (404 Not Found):**
```json
{
  "timestamp": "2025-10-25T19:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/workouts/999"
}
```

---

### 5. Delete Workout

**DELETE** `/api/workouts/{id}`

Deletes a workout by its ID.

**Path Parameters:**
- `id` (Long): The workout ID to delete

**Response:**
- **204 No Content**: Workout successfully deleted
- **404 Not Found**: Workout not found

**Example Response (204 No Content):**
```
(Empty response body)
```

**Example Response (404 Not Found):**
```json
{
  "timestamp": "2025-10-25T19:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/workouts/999"
}
```

## Error Handling

### Common Error Responses

All error responses follow the same format:

```json
{
  "timestamp": "2025-10-25T19:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/workouts"
}
```

### Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Request successful, no content returned
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Validation Errors

When request validation fails, the server returns a 400 Bad Request with details about the validation failure.

## Example Usage

### Complete Workflow

start server with: mvn spring-boot:run -pl server

1. **Get all workouts** (initially empty):
   ```bash
   GET /api/workouts
   # Response: []
   ```

2. **Create a new workout**:
   ```bash
   POST /api/workouts
   Content-Type: application/json
   
   {
     "type": "Running",
     "notes": "Morning jog",
     "date": "2025-10-25",
     "time": 1.5
   }
   # Response: 201 Created with workout including ID
   ```

3. **Get the created workout**:
   ```bash
   GET /api/workouts/1
   # Response: 200 OK with workout details
   ```

4. **Update the workout**:
   ```bash
   PUT /api/workouts/1
   Content-Type: application/json
   
   {
     "type": "Swimming",
     "notes": "Pool session",
     "date": "2025-10-25",
     "time": 2.0
   }
   # Response: 200 OK with updated workout
   ```

5. **Delete the workout**:
   ```bash
   DELETE /api/workouts/1
   # Response: 204 No Content
   ```

6. **Verify deletion**:
   ```bash
   GET /api/workouts
   # Response: []
   ```

## CORS Configuration

The API is configured to allow cross-origin requests from any origin (`*`). This enables the JavaFX client application to communicate with the REST API.

## Data Persistence

All workout data is persisted to a JSON file (`data/workouts.json`) using the existing WorkoutFileManager. The data format is compatible with the existing JavaFX application.

## Server Information

- **Framework**: Spring Boot 3.3.4
- **Port**: 8080
- **Base Path**: `/api`
- **Content Type**: `application/json`
- **CORS**: Enabled for all origins
