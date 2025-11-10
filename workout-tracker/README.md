
# Workout Tracker App

## Description
The Workout Tracker is a client-server application for tracking workout sessions. The application consists of a Spring Boot REST server that exposes workout APIs and a JavaFX client that consumes them. Users can register workouts with exercises, view previously completed workouts, and edit workout details. Data is persisted to JSON files via the server's REST API. The application uses a modular architecture with separate modules for core domain logic, persistence, UI, and server components.

## Expected functionality
- Add a new workout with type, date, duration, and notes
- Add exercises (sets) to workouts with name, repetitions, and weight
- Show a list of all workouts in the UI
- Edit existing workouts and their exercises
- Save and load workouts via REST API (persisted to JSON file on server)

## User Stories
- **User story 1:** As a user, I want to add a workout to track my progress.
- **User story 2:** As a user, I want to be able to see previously completed workouts so I can monitor my own progress.
- **User Story 3:** As a user, I want to add specific exercises to my workouts.
- **User story 4:** As a user, I want to be able to see which exercises i have done in a workout.
- **User Story 5:** As a user, I want to be able to edit workouts after I have created them.

## Quick Start
The Workout Tracker is a client-server application consisting of a Spring Boot REST server and a JavaFX client. Users can add workouts with exercises, edit workout details, and view their workout history. 

### Clean install without tests
```bash
cd workout-tracker
mvn clean install -DskipTests
```

### Set up springboot server
```bash
cd workout-tracker
mvn spring-boot:run -pl server
```

### How to run
Open a new terminal and
```bash
cd workout-tracker
mvn javafx:run -pl ui
```

### How to test
```bash
cd workout-tracker
mvn test
```

To run full verification (tests, static analysis, style checks, coverage):
```bash
cd workout-tracker
mvn verify
```
JaCoCo coverage report: open `target/site/jacoco/index.html` inside each module (e.g., `core/target/site/jacoco/index.html`).

## Basic Usage
1. **Start the Server**: Run the Spring Boot server (see setup instructions above)
2. **Start the Client**: Run the JavaFX client in a separate terminal
3. **Add a Workout**: Fill in the type, select date and duration (hours/minutes), add notes (minimum 3 characters)
4. **Add Exercises**: Click on a workout to open the exercise editor, then add exercises with name, repetitions, and weight
5. **Edit Workouts**: Double-click header fields (type, date, duration, notes) to edit them
6. **Edit Exercises**: Double-click table cells to edit exercise details inline
7. **Reorder Exercises**: Drag and drop rows in the exercise table to reorder them
8. **Save Changes**: Click "Save" to persist workouts via REST API to `data/workouts.json`
9. **Discard Changes**: Click "Back" to discard unsaved edits

## Features
- ✅ **Client-Server Architecture**: Spring Boot REST server with JavaFX client
- ✅ **REST API**: Full CRUD operations via `/api/workouts` endpoints
- ✅ **Modular Design**: Separate modules for core, persistence, UI, and server
- ✅ **Exercise Management**: Add, edit, and reorder exercises within workouts
- ✅ **Exercise Editor**: Inline editing, drag-and-drop reordering, staged edits
- ✅ **Add new workouts** with validation (type, date, duration, notes)
- ✅ **View workout history** in table format
- ✅ **Edit workouts** with double-click to edit header fields
- ✅ **Input validation** and error handling with user-friendly alerts
- ✅ **Modern JavaFX user interface** with responsive design
- ✅ **Code Quality Tools**: JaCoCo (coverage), SpotBugs (static analysis), Checkstyle (style)

For detailed documentation, see 
- [Release 1 Documentation](../docs/release1/release1.md)
- [Release 2 Documentation](../docs/release2/release2.md)
- [Release 3 Documentation](../docs/release3/release3.md)
