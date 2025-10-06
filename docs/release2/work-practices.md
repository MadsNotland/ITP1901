
# Work Practices

## Coding Habits

### Naming Conventions
- **Classes**: PascalCase (e.g., `WorkoutTracker`, `WorkoutTrackerController`)
- **Methods**: camelCase with descriptive names (e.g., `addWorkout()`, `validWorkoutData()`)
- **Variables**: camelCase (e.g., `workoutTracker`, `workoutFileManager`)
- **Packages**: lowercase with dots (e.g., `core`, `persistence`, `ui`)

### Code Formatting
- **Indentation**: 4 spaces (configured in Checkstyle)
- **Braces**: Opening brace on same line, closing brace on new line
- **Imports**: No star imports, unused imports are flagged
- **Whitespace**: Proper spacing around operators and after keywords
- **File endings**: Newline at end of each file

### Architecture Principles
- **Modular design**: Keep domain (`core`), `persistence`, and `ui` decoupled
- **Controller vs. domain**: UI orchestrates; domain holds logic; persistence handles I/O
- **Validation first**: Validate inputs early with clear, actionable messages
- **Defensive programming**: Null checks and parameter validation on public boundaries

### Code Quality
- Follow formatting and naming conventions consistently across modules
- Prefer small, self-contained changes with clear intent
- See [code-quality.md](code-quality.md) for tools, configuration, and reports (Checkstyle, SpotBugs, JaCoCo)

## Testing Practices

### Principles
- Use AAA (Arrange, Act, Assert); one behavior per test
- Mirror production structure for tests (e.g., `WorkoutTest`, `WorkoutTrackerTest`)
- Name tests clearly: `method_expectedBehavior_whenCondition()`

### Focus Areas
- Constructor and parameter validation
- Edge cases and error handling
- Happy paths for core flows

### Types
- Unit tests for domain logic
- Integration tests for module boundaries (e.g., file I/O)
- UI tests where critical user flows need automation

Reference: See [code-quality.md](code-quality.md) for frameworks, plugins, and coverage reports.

## Collaboration Principles
- Communicate early; prefer small PRs with clear scope
- Use GitHub Issues and PRs for traceability; keep discussions on-record
- Respect review time; avoid last-minute merges
- See [workflow.md](workflow.md) for meeting cadence, boards, milestones, and branching

### Code Reviews
- At least one reviewer required
- Focus on clarity, tests, and alignment with architecture principles
- Use repository templates; see [workflow.md](workflow.md) for process specifics

### Communication Style
- **Formal**: Structured meetings for planning and major decisions
- **Informal**: Messenger for quick questions and coordination
- **Documentation**: Clear, concise documentation with bullet points for readability

## Commit & Merge Practices
- Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) and keep changes atomic
- Full process (branching, PR requirements, templates) is in [workflow.md](workflow.md)

## Documentation Practices
- Keep JavaDoc for public APIs; add concise comments for non-obvious logic
- Update docs alongside code changes; prefer clear, scannable Markdown
- For design, see [release2.md](release2.md) and [class-diagram.md](class-diagram.md); for persistence details, see [persistence.md](persistence.md)

## Quality Assurance
- Automated checks run in the build; see [code-quality.md](code-quality.md) for configuration and reports
- Peer reviews focus on correctness, tests, and clarity
- Iterate on tools and process after each release (continuous improvement)
