
# Persistence

This file documents the file format for user data and the chosen persistence model in the **Workout Tracker** application.

## Overview

- **Code handling storage**: `workout-tracker/persistence/src/main/java/persistence/WorkoutFileManager.java`
- **UI that triggers load/save**:
  - Loading at startup in `ui/WorkoutTrackerController.initialize()` (via `WorkoutFileManager.loadWorkouts()`).
  - Manual save via button in `ui/WorkoutTrackerView.fxml` → `WorkoutTrackerController.handleSave()`.
- **Default data file path**: `workout-tracker/data/workouts.json`
- **Settings**: No separate settings file at the moment.

## File format (user data)

- **Format**: JSON
- **Structure**: The entire file is a **list** of `Workout` objects.
- **Fields per element** (from `core/Workout.java`):
  - `type`: `string`
  - `notes`: `string`
  - `date`: `array[int,int,int]` = `[YYYY, MM, DD]` (from Jackson `JavaTimeModule`’s default for `LocalDate`)
  - `time`: `number` (hours)

**Example** (from `data/workouts.json`):

```json
[
  {
    "type": "Pull",
    "notes": "Workout Session 2",
    "date": [
      2025,
      10,
      2
    ],
    "time": 1.0
  },
  {
    "type": "Push",
    "notes": "Workout Session 1",
    "date": [
      2025,
      9,
      30
    ],
    "time": 3.0
  }
]
```

## Persistence model
- **Choice**: Hybrid
- **Implicit load**: On startup, any existing sessions are automatically loaded from workouts.json.
- **Explicit save**: The user must press the Save button to write the current list to file (no autosave).

## Rationale:
- Minimal friction at startup (data appears without user action).
- Clear control and a “milestone” feeling when saving (the user knows when the file is updated).
- Reduces the risk of overwriting with half-finished changes compared to pure autosave.

## Trade-offs and alternatives
**Document/explicit (manual only)**
- Clear save points, good for version control.
- Requires the user to remember to save; risk of data loss on crashes.

**Implicit/app (autosave only)**
- Zero friction; lower risk of loss on crashes.
- Less predictability; harder to “discard” work.

**Hybrid (chosen)**
- Combines easy startup with deliberate saving.
- Changes not saved before exit can be lost.

## Why JSON?
- Advantages: Human-readable, Git-friendly diffs, easy export/import, broad library support (Jackson).
- Disadvantages: No comments in the format; can become heavy as a single large file for very big datasets.

**Considered alternatives (brief)**:
- YAML: More human-friendly syntax, but parsing is more fragile.
- SQLite: Scales well and enables queries; poorer git-diff and “document” workflow.
- Binary/Protobuf: Compact and versionable; not human-readable.

## Technical details
- Read/write: Performed in WorkoutFileManager using ObjectMapper. LocalDate is supported via JavaTimeModule.
- File path resolution: WorkoutTrackerController.initialize() resolves workouts.json relative to the project directory (with sensible fallbacks).
- Validation: core.Workout validates fields in constructors/setters; deserialized objects must pass this validation.
