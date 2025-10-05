
# Release 2

## Overview

This release focuses on improving documentation quality, establishing clear development practices, and extending system documentation to meet project requirements. Functional improvements are secondary; the main goal is to ensure maintainability, traceability, and clarity of design choices.

## Summary of Changes
Documentation Updates
- [Root README](../../README.md): Updated to describe the current application functionality and architecture overview.
- [Architecture Diagram](class-diagram.md): Added a Mermaid class diagram to visualize system components and interactions (see viewing instructions in that file).
- Release-Specific Documentation: All release-related documents are placed under `docs/release2/` (this directory).

New Documentation Files
- [work-practices.md](work-practices.md): Describes team’s coding habits, testing approach, and collaboration practices.
- [workflow.md](workflow.md): Explains the team’s workflow, including branching strategy, code review, and task management.
- [code-quality.md](code-quality.md): Lists tools and configurations used for maintaining code quality.
- [ai-tools.md](../ai-tools.md): Updated to reflect how AI tools are currently used in the project.
- [teamwork.md](teamwork.md): Reflection on planning, coordination, and lessons learned during development.
- [persistence.md](persistence.md): Defines the chosen persistence approach, file format for user data/settings, and reflection on the chosen persistence model (document vs. implicit vs. hybrid).
- release2.md (this file)

Codebase Documentation
- [Core module](../../workout-tracker/core/): Updated comments and Javadoc for clarity and accuracy.
- [Persistence module](../../workout-tracker/persistence/): Updated comments and Javadoc to match the implemented persistence logic.
- [UI module](../../workout-tracker/ui/): Improved inline documentation and Javadoc for maintainability.

Structure and Organization
- Codebase root renamed from `app/` to `workout-tracker/` for clarity and consistency.
- Established a modular Maven project with three layers/modules:
  - `core` (domain logic)
  - `persistence` (file-based JSON storage)
  - `ui` (presentation)
- See [Root README](../../README.md) for updated repository structure.
- Separated domain logic from UI and persistence to improve reusability and maintainability.
- Renamed several files to be more descriptive and self-explanatory.

Build and Quality Tooling
- Maven configurations updated to include:
  - JaCoCo for test coverage reporting
  - SpotBugs for static analysis
  - Checkstyle for code style enforcement
  (module-specific plugin configuration is defined in each module's `pom.xml` as appropriate)

Focus and Intent
- Release 2 prioritizes documentation completeness, clarity of architecture, and reflection on process choices. These improvements ensure the project meets assessment criteria and provides a solid foundation for future releases.
