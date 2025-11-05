
# IT1901 – Workout Tracker App

## Group 28: *Alexandra Hügli, Konrad Hogstad Johannessen, Trym Andreas Johnsen, Martin Kringstad, Mads Nøtland, David Rossavik*

### Overview
This is the group project for *IT1901 Informatikk Prosjektarbeid I* (Fall 2025).

Our team is developing a Workout Tracker in JavaFX over three planned releases. The application allows users to track their workout sessions with type, duration, date, and notes, with full data persistence capabilities.

[Open repository in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-2025-groups/gr2528.git?new)

```bash
How to run:

cd workout-tracker/server
mvn spring-boot:run

then open a new terminal and

cd workout-tracker/ui
mvn javafx:run
```

### Documentation
- [Release 1 Documentation](docs/release1/release1.md): Complete feature overview and technical details
- [Release 2 Documentation](docs/release2/release2.md): Overview over modularisation, code quality, documentation, architecture, work habits
- [Application README](workout-tracker/README.md): Quick start guide and basic usage

### Requirements
- Java: 17
- Apache Maven: 3.8+
- Dependencies: JavaFX, JUnit 5, TestFX

### Repository structure
    .
    ├── workout-tracker/            # Multi-module Maven project
    │   ├── pom.xml                    # Parent POM (packaging=pom)
    │   ├── core/                      # Core module (domain logic)
    │   │   ├── pom.xml                    # Core module POM
    │   │   ├── src/main/java/core/        # Core business logic
    │   │   └── src/test/java/core/        # Core unit tests
    │   ├── ui/                        # UI module (JavaFX)
    │   │   ├── pom.xml                    # UI module POM
    │   │   ├── src/main/java/ui/          # JavaFX UI components
    │   │   ├── src/main/resources/ui/     # FXML files and resources
    │   │   └── src/test/java/ui/          # UI tests (TestFX)
    │   ├── persistence/               # Persistence module
    │   │   ├── pom.xml                    # Persistence module POM
    │   │   ├── src/main/java/persistence/ # Data persistence logic
    │   │   └── src/test/java/persistence/ # Persistence tests
    │   └── data/                      # Data files
    │       └── workouts.json              # Example data file
    ├── docs/                      # Documentation and mockups
    │   ├── release1                   # Release 1
    │   │   ├── release1.md                # Release 1 documentation
    │   │   └── screenshots/               # Screenshots and mockups
    │   ├── release2/                  # Release 2
    │   │    ├── classDiagram.md            # Class diagram
    │   │    ├── release2.md                # Release 2 documentation
    │   │    └── # more documentation...
    │   └── ai-tools.md                # AI usage documentation
    ├── devfile.yaml               # devfile for Eclipse Che
    └── README.md                  # This file

### Releases
- Release 1: Basic workout creation, list view, save/load
- Release 2: Modularisation, code quality, documentation, architecture, work habits
