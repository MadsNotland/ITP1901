package ui;

import java.time.LocalDate;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Empty controller - ready for your implementation
 */
public class AppController {

    @FXML
    private ComboBox<Integer> comboBoxHours;
    @FXML
    private ComboBox<Integer> comboBoxMinutes;
    @FXML
    private TextField typeField;
    @FXML
    private TextArea notesField;
    @FXML
    private DatePicker datePicker;

    private WorkoutTracker workoutTracker;


    @FXML
    void initialize() {
        initializeComboBoxes();
        workoutTracker = new WorkoutTracker();
    }

    // "ADD WORKOUT"-BUTTON //
    @FXML
    private void handleAddWorkout() {
        try {
            // Retract fields //
            String type = typeField.getText();
            String notes = notesField.getText();
            LocalDate date = datePicker.getValue();

            // Validate fields //
            if (type == null || type.trim().isEmpty()) {
                showError("Type must not be empty");
                return;
            }
            if (date == null) {
                showError("Date must not be empty");
                return;
            }
            if (notes == null || notes.trim().length() < 3) {
                showError("Notes must be at least 3 characters long.");
                return;
            }
            if (comboBoxHours.getValue() == null || comboBoxMinutes.getValue() == null) {
                showError("Please select both hours and minutes.");
                return;
            }
            // Time must be validated before input. NOTE: minutes is divided by 100 for visual representation
            double time = (double) comboBoxHours.getValue() + (comboBoxMinutes.getValue() / 100.0);
            System.out.println(time);


            // Create new Workout-object and add to WorkoutTracker //
            Workout newworkout = new Workout(type, notes, date, time);
            workoutTracker.addWorkout(newworkout);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        System.out.println(workoutTracker.getAllWorkouts()); // Validate workout added
    }

    private void initializeComboBoxes() {
        comboBoxHours.getItems().clear();
        for (int hours = 0; hours <= 10; hours++) {
            comboBoxHours.getItems().add(hours);
        }

        comboBoxMinutes.getItems().clear();
        for (int minutes = 5; minutes < 60; minutes = minutes += 5){
            comboBoxMinutes.getItems().add(minutes);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
