# Work Practices

## Coding Habits

### Naming Conventions
- **Classes**: PascalCase (e.g., `WorkoutTracker`, `AppController`)
- **Methods**: camelCase with descriptive names (e.g., `addWorkout()`, `validWorkoutData()`)
- **Variables**: camelCase (e.g., `workoutTracker`, `fileManager`)
- **Packages**: lowercase with dots (e.g., `core`, `persistence`, `ui`)

### Code Formatting
- **Indentation**: 4 spaces (configured in Checkstyle)
- **Braces**: Opening brace on same line, closing brace on new line
- **Imports**: No star imports, unused imports are flagged
- **Whitespace**: Proper spacing around operators and after keywords
- **File endings**: Newline at end of each file

### Architecture Patterns
- **Modular Design**: Multi-module Maven project with clear separation (`core`, `ui`, `persistence`)
- **MVC Architecture**: Clear separation between UI controller, business logic, and data persistence
- **Validation**: Comprehensive input validation with meaningful error messages
- **Defensive Programming**: Null checks and parameter validation in all public methods

### Code Quality Tools
- **Checkstyle**: Enforces formatting and coding standards
- **SpotBugs**: Static analysis for potential bugs
- **JaCoCo**: Code coverage reporting
- All tools run automatically during Maven build process

## Testing Approach

### Testing Framework
- **Unit Tests**: JUnit 5 with comprehensive test coverage
- **UI Tests**: TestFX for JavaFX application testing
- **Test Structure**: Follow AAA pattern (Arrange, Act, Assert)

### Test Organization
- **Test Classes**: Mirror production class structure (`WorkoutTest`, `WorkoutTrackerTest`)
- **Helper Methods**: Reusable test data creation methods (e.g., `validWorkout()`)
- **Test Naming**: Descriptive method names indicating scenario and expected outcome
  - Format: `methodName_expectedBehavior_whenCondition()`
  - Examples: `constructor_throws_whenTypeIsNullOrBlank()`, `addWorkout_success_addsRowToTable()`

### Coverage Requirements
- **Target**: High coverage of core business logic
- **Focus Areas**: 
  - Constructor validation
  - Method parameter validation
  - Edge cases and error conditions
  - Happy path scenarios
- **Tools**: JaCoCo generates coverage reports in HTML format

### Test Types
- **Unit Tests**: Test individual methods and classes in isolation
- **Integration Tests**: Test component interactions (e.g., file persistence)
- **UI Tests**: Test user interface interactions and validation
- **Temporary Files**: Use `@TempDir` for file-based tests to avoid side effects

## Collaboration

### Meeting Structure
- **Frequency**: Twice weekly, 2-3 hours per session
- **Format**: Planning sessions, working sessions, and progress reviews
- **Communication**: Facebook Messenger for quick coordination outside meetings

### Task Management
- **Platform**: GitHub Issues with Kanban board in Project view
- **Milestones**: Each release gets its own milestone (Release1, Release2)
- **Assignment**: First-come-first-served approach with trust-based workload distribution
- **Planning**: Initial meeting to create issues and clarify tasks for each release

### Code Reviews
- **Requirement**: Minimum one reviewer per pull request
- **Templates**: Standardized PR and issue templates for consistency
- **Focus Areas**: 
  - Code quality and adherence to conventions
  - Test coverage
  - Documentation completeness
  - Architecture alignment

### Communication Style
- **Formal**: Structured meetings for planning and major decisions
- **Informal**: Messenger for quick questions and coordination
- **Documentation**: Clear, concise documentation with bullet points for readability

## Commit & Merge Practices

### Commit Message Style
- **Standard**: Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- **Format**: `type(scope): description`
- **Examples**: 
  - `feat(ui): add workout validation`
  - `fix(persistence): handle file not found exception`
  - `docs(readme): update installation instructions`

### Commit Frequency
- **Approach**: Regular, small commits rather than large batches
- **Frequency**: After completing logical units of work
- **Atomicity**: Each commit should represent a complete, testable change

### Branch Strategy
- **Main Branch**: `main` for stable, production-ready code
- **Feature Branches**: Created for each issue or feature
- **Naming**: Descriptive branch names related to issue numbers or features

### Pull Request Process
- **Review Requirement**: All PRs require at least one reviewer
- **Templates**: Use standardized PR template with description, motivation, and testing sections
- **Testing**: All tests must pass before merge
- **Quality Gates**: Checkstyle, SpotBugs, and test coverage checks must pass

## Documentation

### Design Documentation
- **Location**: `docs/release2/` directory
- **Class Diagrams**: Mermaid diagrams showing relationships between classes
- **Architecture**: Clear documentation of module separation and dependencies
- **API Documentation**: JavaDoc comments for all public methods and classes

### Reflection and Process Documentation
- **Teamwork**: Regular reflection on collaboration effectiveness
- **Workflow**: Documentation of meeting structure and task management
- **AI Usage**: Transparent documentation of AI tool usage and limitations

### Code Documentation
- **JavaDoc**: Comprehensive documentation for all public APIs
- **Inline Comments**: Explanatory comments for complex logic
- **README Files**: Module-specific documentation for setup and usage

### Documentation Standards
- **Format**: Markdown with clear structure and bullet points
- **Clarity**: Concise, well-structured text for team and external reviewers
- **Maintenance**: Documentation updated alongside code changes
- **Accessibility**: Easy to read and understand for team members and reviewers

## Quality Assurance

### Automated Checks
- **Build Process**: Maven ensures all quality tools run on every build
- **Checkstyle**: Enforces coding standards automatically
- **SpotBugs**: Identifies potential bugs and code smells
- **Test Coverage**: JaCoCo reports ensure adequate test coverage

### Manual Reviews
- **Peer Review**: All code changes reviewed by at least one team member
- **Architecture Review**: Major changes discussed in team meetings
- **Documentation Review**: All documentation reviewed for clarity and completeness

### Continuous Improvement
- **Regular Reflection**: Team evaluates and improves practices after each release
- **Tool Updates**: Regular updates to development tools and dependencies
- **Process Refinement**: Workflow adjustments based on team experience and feedback
