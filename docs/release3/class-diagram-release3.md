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

%% Core domain
class Exercise {
  - name : String
  - repetitions : int
  - weight : double
  - id : final String
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
class WorkoutTracker {
  - workouts : final ObservableList~Workout~
  + addWorkout(workout : Workout)
  + getAllWorkouts() : ObservableList~Workout~
}

%% Persistence
class WorkoutFileManager {
  + saveWorkouts(workouts : List~Workout~)
  + loadWorkouts() : List~Workout~
}

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

%% server.WorkoutService represents the Spring service in the server module
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
Workout "1" *-- "0..*" Exercise : contains
WorkoutTracker o-- "*" Workout
WorkoutFileManager ..> Workout : serializes

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

ServerWorkoutService --> WorkoutFileManager
WorkoutController --> ServerWorkoutService
```
