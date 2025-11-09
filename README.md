
# IT1901 – Workout Tracker App

## Group 28: *Alexandra Hügli, Konrad Hogstad Johannessen, Trym Andreas Johnsen, Martin Kringstad, Mads Nøtland, David Rossavik*

### Overview
This is the group project for *IT1901 Informatikk Prosjektarbeid I* (Fall 2025).

Our team has developed a Workout Tracker in JavaFX over three releases. The application allows users to track their workout sessions with type, duration, date, and notes, with full data persistence capabilities.

## Using Eclipse Che

[Open repository in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-2025-groups/gr2528.git?new)

### Opening the Desktop UI

1. In the left sidebar, click **"Endpoints"**
2. Find and click **"6080-desktop-ui"** (or **"desktop-ui-6080"**)
3. If you see a "404 Not Found" error, wait a moment and refresh the page. The desktop server takes some time to start up
4. The noVNC login screen will prompt for a password

### Getting the VNC Password

The VNC password is automatically generated and displayed when you open a terminal in Eclipse Che. The password is also stored in `/tmp/vnc-password.txt` and will be printed automatically when you open a new terminal.

**To get the password:**
- Open any terminal in Eclipse Che
- The password will be displayed automatically in the terminal output
- Copy the password and paste it into the noVNC login screen

### Running the Application

Once you're in the desktop environment, see the [Application README](workout-tracker/README.md) for detailed instructions on how to run the application.

**Quick start:**
```bash
cd workout-tracker
mvn clean install -DskipTests
mvn spring-boot:run -pl server
```

Then open a new terminal and:
```bash
cd workout-tracker
mvn javafx:run -pl ui
```

### Documentation
- [Release 1 Documentation](docs/release1/release1.md): Complete feature overview and technical details
- [Release 2 Documentation](docs/release2/release2.md): Overview over modularisation, code quality, documentation, architecture, work habits
- [Release 3 Documentation](docs/release3/release3.md): Architectural separation (Spring Boot REST server + JavaFX client), exercise editor, REST API integration, domain model enhancements 
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
    │   │   ├── src/main/java/core/service # Business logic (service and data flow)   
    │   │   ├── src/test/java/core/service # Business logic tests
    │   │   ├── src/main/java/core/        # Domain model (entities and validation)
    │   │   └── src/test/java/core/        # Domain model tests   
    │   ├── ui/                        # UI module (JavaFX)
    │   │   ├── pom.xml                    # UI module POM
    │   │   ├── src/main/java/ui/base      # Shared base controller
    │   │   ├── src/main/java/ui/          # View controllers for FXML
    │   │   ├── src/main/resources/ui/     # FXML files and resources
    │   │   └── src/test/java/ui/          # UI tests (TestFX)
    │   ├── persistence/               # Persistence module
    │   │   ├── pom.xml                    # Persistence module POM
    │   │   ├── src/main/java/persistence/ # Data persistence logic
    │   │   └── src/test/java/persistence/ # Persistence tests
    │   ├── server/                    # Server module
    │   │   ├── pom.xml                    # Server module POM
    │   │   └── src/main/java/server/      # Server logic
    │   ├── data/                      # Data files
    │   │   └── workouts.json              # Default data file (persistence storage)
    │   └── README.md                  # README for project repository
    ├── docs/                      # Documentation and mockups
    │   ├── release1                   # Release 1
    │   │   ├── release1.md                # Release 1 documentation
    │   │   └── # more documentation...
    │   ├── release2/                  # Release 2
    │   │   ├── release2.md                # Release 2 documentation
    │   │   └── # more documentation...
    │   └── release3/                  # Release 3
    │       ├── release3.md                # Release 3 documentation
    │       └── # more documentation...
    ├── devfile.yaml               # devfile for Eclipse Che
    └── README.md                  # This file

### Releases
- Release 1: Basic workout creation, list view, save/load
- Release 2: Modularisation, code quality, documentation, architecture, work habits
- Release 3: Architectural separation (Spring Boot REST server + JavaFX client), REST API with full CRUD, exercise editor view, domain model updates 
