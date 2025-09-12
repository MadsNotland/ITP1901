package ui;

import java.time.LocalDate;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
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

    @FXML
    private void handleAddWorkout() {
        try {
            String type = typeField.getText();
            String notes = notesField.getText();
            LocalDate date = datePicker.getValue();
            double time = (double) comboBoxHours.getValue() + comboBoxMinutes.getValue();

            // Oprett nytt workout-objekt
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
        for (int minutes = 0; minutes < 60; minutes = minutes += 5){
            comboBoxMinutes.getItems().add(minutes);
        }

        
    }
}
