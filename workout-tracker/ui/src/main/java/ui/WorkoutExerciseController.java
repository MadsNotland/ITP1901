package ui;

import java.io.IOException;

import core.Workout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import persistence.WorkoutFileManager;

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

    @FXML
    private void handleBack() throws IOException {
        WorkoutTrackerApp.setRoot("WorkoutTrackerView");
    }
}
