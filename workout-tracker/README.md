
# Workout Tracker App

## Description
The Workout Tracker is a simple training diary where users can register workouts and view previously completed ones.
The app supports saving workouts to JSON so they can be loaded again later.

## Expected functionality (Release 1)
- Add a new workout with title, description, date and time.
- Show a list of all workouts in the UI.
- Save and load workouts from a file.

## User Stories
- **User story 1:** As a user, I want to add a workout to track my progress.
- **User story 2:** As a user, I want to be able to see previously completed workouts so I can monitor my own progress.

## Quick Start
The Workout Tracker is a JavaFX application for tracking workout sessions. Users can add workouts with type, duration, date, and notes, then view their workout history in a table format.

## Clean install without tests
```bash
cd workout-tracker
mvn clean install -DskipTests
```

## Set up springboot server
```bash
cd workout-tracker
mvn spring-boot:run -pl server
```

## How to run
Open a new terminal and
```bash
cd workout-tracker
mvn javafx:run -pl ui
```

## How to test
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
1. **Add a Workout**: Fill in the type, select date and time, add notes (minimum 3 characters)
2. **View Workouts**: All workouts appear in the table below the form
3. **Save Data**: Click "Save" to persist workouts to `data/workouts.json`

## Features
- ✅ Add new workouts with validation
- ✅ View workout history in table format
- ✅ Save/load workouts from JSON file
- ✅ Input validation and error handling
- ✅ Modern JavaFX user interface

For detailed documentation, see [Release 1 Documentation](../../docs/release1/release1.md) and [Release 2 Documentation](../../docs/release2/release2.md).
