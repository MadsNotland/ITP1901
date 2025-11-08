<!-- How to view -->
<!--
Viewing options:
1) GitHub renders Mermaid automatically when viewing this file in the repo UI.
2) VS Code with the "Markdown Preview Mermaid Support" extension will render it in the Markdown preview.
3) Use Mermaid Live Editor: copy the code block below into https://mermaid.live and view/export. You will have to remove the first line "``` mermaid" to view it.
-->
```mermaid
classDiagram
direction TB

%% UI layer
class MainController {
  + showWorkoutTable()
  + showWorkoutForm()
  + showExerciseTable(workout : Workout)
}
class WorkoutTableController {
  + initializeData()
  + handleAddButton()
}
class WorkoutFormController {
  + handleAddWorkout()
  + handleAddExercise()
}
class ExerciseTableController {
  + setWorkout(workout : Workout)
  + handleSave()
}

%% Application services
class WorkoutService {
  + loadWorkouts()
  + addWorkout(workout : Workout)
  + updateWorkout(id : Long, workout : Workout)
  + getWorkoutTracker() : WorkoutTracker
}
class WorkoutClient {
  <<interface>>
  + getAllWorkouts() : List~Workout~
  + createWorkout(workout : Workout) : Workout
  + updateWorkout(id : Long, workout : Workout) : Workout
  + deleteWorkout(id : Long) : void
}
class RestWorkoutClient {
  + getAllWorkouts() : List~Workout~
  + createWorkout(workout : Workout) : Workout
  + updateWorkout(id : Long, workout : Workout) : Workout
  + deleteWorkout(id : Long) : void
}

%% Domain
class WorkoutTracker {
  - workouts : final ObservableList~Workout~
  + addWorkout(workout : Workout)
  + getAllWorkouts() : ObservableList~Workout~
}
class Workout {
  - id : Long
  - date : LocalDate
  - time : double
  - notes : String
  - type : String
  - exercises : final List~Exercise~
  + getExercises() : List~Exercise~
  + addExercise(exercise : Exercise) : void
  + removeExercise(exercise : Exercise) : void
  + findExerciseById(id : String) : Exercise
}
class Exercise {
  - name : String
  - repetitions : int
  - weight : double
  - id : final String
}

%% Persistence & server
class WorkoutFileManager {
  + saveWorkouts(workouts : List~Workout~)
  + loadWorkouts() : List~Workout~
}
class ServerWorkoutService {
  + getAllWorkouts() : List~Workout~
  + createWorkout(workout : Workout) : Workout
  + updateWorkout(id : Long, workout : Workout) : Workout
  + deleteWorkout(id : Long) : boolean
}
class WorkoutController {
  + getAllWorkouts()
  + createWorkout(workout : Workout)
  + updateWorkout(id : Long, workout : Workout)
  + deleteWorkout(id : Long)
}

%% Relationships
MainController --> WorkoutService
MainController ..> WorkoutTableController
MainController ..> WorkoutFormController
MainController ..> ExerciseTableController
WorkoutTableController --> WorkoutService
WorkoutFormController --> WorkoutService
ExerciseTableController --> WorkoutService

WorkoutService --> WorkoutTracker
WorkoutService --> WorkoutClient
RestWorkoutClient ..|> WorkoutClient
RestWorkoutClient ..> Workout

WorkoutTracker o-- "*" Workout
Workout "1" *-- "0..*" Exercise
WorkoutFileManager ..> Workout : serializes

ServerWorkoutService --> WorkoutFileManager
WorkoutController --> ServerWorkoutService
```
