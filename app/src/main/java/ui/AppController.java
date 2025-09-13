package ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Workout;
import core.WorkoutTracker;
import core.persistence.WorkoutFileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Empty controller - ready for your implementation
 */
public class AppController {

    // Input-components //
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

    // TableView-components //
    @FXML
    private TableView<Workout> workoutTable;
    @FXML
    private TableColumn<Workout, String> typeColumn;
    @FXML
    private TableColumn<Workout, LocalDate> dateColumn;
    @FXML
    private TableColumn<Workout, Double> timeColumn;
    @FXML
    private TableColumn<Workout, String> notesColumn;

    // WorkoutTracker //
    private WorkoutTracker workoutTracker;

    // WorkoutFileManager //
    private WorkoutFileManager fileManager;


    @FXML
    void initialize() {
        initializeComboBoxes();
        workoutTracker = new WorkoutTracker();
        setTableView();
        fileManager = new WorkoutFileManager(Path.of("app/data/workouts.csv"));
        try {
            var loaded = fileManager.loadWorkouts();
            workoutTracker.getAllWorkouts().setAll(loaded);
        } catch (Exception e) {
            showLoadError("Could not load workouts from file "+e.getMessage());
        }
    }

    // "Save" -button //
    @FXML
    private void handleSave(){
        List<Workout> workouts = new ArrayList<>(workoutTracker.getAllWorkouts());
        try {
            fileManager.saveWorkouts(workouts);
            showInfo("Saved", "Workouts saved to workouts.csv");
        } catch (Exception e) {
            showError("Could not save workouts to file: " + e.getMessage());
        }
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
            if (comboBoxHours.getValue() == null || comboBoxMinutes.getValue() == null || comboBoxHours.getValue() + comboBoxMinutes.getValue() == 0) {
                showError("Please select both hours and minutes.");
                return;
            }
            // Time must be validated before input. NOTE: minutes is divided by 100 for visual representation
            double time = (double) comboBoxHours.getValue() + (comboBoxMinutes.getValue() / 100.0);

            // Create new Workout-object and add to WorkoutTracker //
            Workout newworkout = new Workout(type, notes, date, time);
            workoutTracker.addWorkout(newworkout);
            // workoutTable.setItems(workoutTracker.getAllWorkouts()); // Oppdatere TableView

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // TABLEVIEW //
    private void setTableView() {
        // Connecting TableView to ObservableList workouts
        workoutTable.setItems(workoutTracker.getAllWorkouts()); 

        // Connecting tableColumns to fields in workout
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }


    // Make ComboBoxes-content for time //
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

    // ERROR-MESSAGE //
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Confirm saved info//
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Load error message//
    private void showLoadError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
