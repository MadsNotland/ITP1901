package ui;


import core.Workout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WorkoutExerciseController {
    private Workout workout;

    @FXML private Label titleLabel; // example node in your FXML

    public void setWorkout(Workout workout) {
        this.workout = workout;
        // Populate UI now that we have the model
        if (titleLabel != null && workout != null) {
            titleLabel.setText(workout.getType() + " on " + workout.getDate());
        }
        // ... load exercises for this workout, bind tables, etc.
    }

    @FXML
    private void initialize() {
        // optional: init static UI here.
    }
}
