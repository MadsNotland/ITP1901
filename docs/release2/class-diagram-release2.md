<!-- How to view -->
<!--
Viewing options:
1) GitHub renders Mermaid automatically when viewing this file in the repo UI.
2) VS Code with the "Markdown Preview Mermaid Support" extension will render it in the Markdown preview.
3) Use Mermaid Live Editor: copy the code block below into https://mermaid.live and view/export. You will have to remove the first line "``` mermaid" to view it.
-->
```mermaid
classDiagram
direction LR

%% --------- Domain core ----------
class Workout {
  - date : LocalDate
  - time : double
  - notes : String
  - type : String
  + Workout(type : String, notes : String, date : LocalDate, time : double)
  + getDate() : LocalDate
  + setDate(date : LocalDate) : void
  + getTime() : double
  + setTime(time : double) : void
  + getNotes() : String
  + setNotes(notes : String) : void
  + getType() : String
  + setType(type : String) : void
  + toString() : String
}

class WorkoutTracker {
  - workouts : ObservableList~Workout~
  + WorkoutTracker()
  + addWorkout(workout : Workout) : void
  + getAllWorkouts() : ObservableList~Workout~
  + getWorkouts() : List~Workout~
}

%% --------- Persistence ----------
class WorkoutFileManager {
  - filePath : Path
  - objectMapper: ObjectMapper
  + WorkoutFileManager(filePath : Path)
  + saveWorkouts(workouts : List~Workout~) : void
  + addWorkout(workout : Workout) : void
  + loadWorkouts() : List~Workout~
}

%% --------- UI Controller (orchestrates) ----------
class WorkoutTrackerController {
  - workoutTracker : WorkoutTracker
  - fileManager : WorkoutFileManager
  + initialize() : void
  + handleSave() : void
  + handleAddWorkout() : void
  ..helpers..
  - setTableView() : void
  - initializeComboBoxes() : void
  - validWorkoutData(type : String, notes : String, date : LocalDate) : boolean
  - showAlert(type : AlertType, title : String, message : String) : void
}

%% --------- Relationships ----------
WorkoutTracker o-- "0..*" Workout : manages
WorkoutFileManager ..> Workout : uses (I/O)

WorkoutTrackerController --> "1" WorkoutTracker
WorkoutTrackerController --> "1" WorkoutFileManager
WorkoutTrackerController ..> Workout : creates/uses

```
