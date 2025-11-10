# Release 3

## Overview

Release 3 establishes the required separation between backend and client components. The application is now organized as a Spring Boot server that exposes the workout API and a JavaFX client that consumes it. Beyond the architectural restructuring, this release adds per-exercise data in the domain model, improves the editing workflow in the client, updates documentation to reflect the new scope and quality requirements, and introduces production-ready packaging via jlink and jpackage

## User stories
For this release we implemented 3 additional user stories:

- **User Story 3:** As a user, I want to add specific exercises to my workouts.
- **User story 4:** As a user, I want to be able to see which exercises I have done in a workout.
- **User Story 5:** As a user, I want to be able to edit workouts after I have created them.

For a complete overview over all user stories, see the [project repository README](../../workout-tracker/README.md).

## Implemented Features

### Architectural Separation
- **Spring Boot REST server**: New `workout-tracker/server` module packages the core domain and persistence layers behind `/api/workouts` endpoints.
- **Shared domain model**: `core` remains the single source of truth for entities and validation, reducing duplication between client and server.
- **File-backed persistence**: The server reuses `WorkoutFileManager` to persist data to `data/workouts.json`, keeping compatibility with earlier releases.

### REST API & Integration
- **Full CRUD endpoints**: `GET`, `POST`, `PUT`, and `DELETE` operations provide complete lifecycle management for workouts (see `rest-api.md` for payloads).
- **ID management**: The server assigns stable numeric IDs while the JavaFX client caches them locally via `WorkoutService`.
- **Error handling & CORS**: Uniform JSON error responses and open CORS configuration allow future clients (web/mobile) to integrate without extra setup.

### JavaFX Client Enhancements
- **REST-backed UI**: `MainController` boots the app by loading workouts from the server and surfaces connection failures with user-facing alerts.
- **Exercise editor view**: New `ExerciseTableView` lets users manage sets for a workout with inline editing, drag-and-drop reordering, staged edits, and undo via “Back”.
- **Form refinements**: Duration pickers now separate hours/minutes, and header fields toggle editability to prevent accidental changes.
- **Custom runtime image**: A jlink build task produces a trimmed Java runtime tailored to the JavaFX client modules for smaller downloads and guaranteed compatibility.
- **Installers via jpackage**: jpackage scripts wrap the client and the jlink image into platform installers (MSI/DMG) under dist/, enabling evaluators to install and launch the app without manual Java setup.
- **CI integration**: Packaging tasks run behind the package-client profile so release pipelines can generate artifacts alongside the existing mvn verify stage.


### Domain Model Updates
- **Exercise entity**: Introduced `core.Exercise` with validation, UUID identifiers, and Jackson annotations for seamless JSON serialization.
- **Workout validation**: `Workout` now enforces non-empty exercise lists, supports ID assignment, and exposes helper methods used by both server and client flows.
- **Service abstraction**: `core.service.WorkoutService` coordinates local state with a `WorkoutClient` interface, easing future testability and alternative client implementations.

## Quality Assurance
- **Automated tests**: Expanded unit tests (e.g., `WorkoutServiceTest`) verify REST integration logic with mock clients alongside existing core tests.
- **Static analysis & coverage**: Checkstyle, SpotBugs, and JaCoCo remain wired for every module; running `mvn verify` at the project root executes style checks, tests, and coverage reports.
- **Che-ready tooling**: Updated instructions in the root `README.md` ensure evaluators can open the repo in Eclipse Che and run the split server/client workflow.

## Documentation Updates
- **Architecture diagrams**: Updated UML artifacts (`class-diagram-release3.md`, `package-diagram/package-diagram.puml/png`, `sequence-diagram.puml`) reflect the new REST layers.
- **Process reflections**: Refreshed `teamwork.md` and `ai-tools.md` capture collaboration habits and responsible AI usage for this milestone.
- **REST reference**: `rest-api.md` documents request/response formats, validation rules, and example flows for the published API.
- **Sustainability focus**: `sustainability/sustainability.md` discusses the project’s sustainability considerations for this iteration.
- **Distribution guides**: New `packaging/README.md` walks through invoking jlink and jpackage, explains artifact locations, and links CI outputs for release managers.


## Screenshots
- Main dashboard (and with information button clicked)

    ![alt text](.\screenshots\image.png)

    ![alt text](.\screenshots\image-4.png)

- Workout creation form

    ![alt text](.\screenshots\image-1.png)

- Workout edit form (and with information button clicked)

    ![alt text](.\screenshots\image-2.png)

    ![alt text](.\screenshots\image-3.png)
