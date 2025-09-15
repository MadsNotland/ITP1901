
# IT1901 – Workout Tracker App

## Group 28: *Alexandra Hügli, Konrad Hogstad Johannessen, Trym Andreas Johnsen, Martin Kringstad, Mads Nøtland, David Rossavik*

### Overview
[Open repository in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-2025-groups/gr2528.git?new) This is the group project for *IT1901 Informatikk Prosjektarbeid I* (Fall 2025).

Our team is developing a Workout Tracker in JavaFX over three planned releases. The application allows users to track their workout sessions with type, duration, date, and notes, with full data persistence capabilities.

### Documentation
- [Release 1 Documentation](docs/release1/release1.md): Complete feature overview and technical details
- [Application README](app/README.md): Quick start guide and basic usage

### Requirements
- Java: 21
- Apache Maven: 3.8+
- Dependencies: JavaFX, JUnit 5, TestFX

### Repository structure
    .
    ├── app/              # Application source and tests
    │   ├── pom.xml
    │   ├── src/main/java/core/...     # Core logic
    │   ├── src/main/java/ui/...       # JavaFX UI
    │   ├── src/test/java/...          # Unit and UI tests
    │   └── data/workouts.csv          # Example data file
    ├── docs/             # Documentation and mockups
    │   ├── release1/ai-tools.md       # AI usage documentation
    │   ├── release1/release1.md       # Release 1 documentation
    │   └── release1/mockup.png        # Mockup of release 1
    ├── devfile.yaml                   # devfile for Eclipse Che
    └── README.md         # This file

### Releases
- Release 1: Basic workout creation, list view, save/load
- Release 2-3: Planned feature expansions
