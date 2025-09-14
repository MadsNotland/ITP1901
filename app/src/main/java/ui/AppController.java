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
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
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


        // Load workouts from file //
        String currentDir = System.getProperty("user.dir"); // Set relative filepath, so it will work for both mvn build and IDE
        if (currentDir == null) throw new IllegalStateException("Cannot determine working directory");
        Path workoutFilePath;
        if (currentDir.endsWith("app")) {
            workoutFilePath = Path.of("data", "workouts.csv");
        } else {
            workoutFilePath = Path.of("app", "data", "workouts.csv");
        }

        fileManager = new WorkoutFileManager(workoutFilePath);
        try {
            var loaded = fileManager.loadWorkouts();
            workoutTracker.getAllWorkouts().setAll(loaded);
        } catch (Exception e) {
            showAlert(ERROR, "Load Error", "Could not load workouts from file "+e.getMessage());
        }
    }

    // "Save" -button //
    @FXML
    private void handleSave() {
        List<Workout> workouts = new ArrayList<>(workoutTracker.getAllWorkouts());
        try {
            fileManager.saveWorkouts(workouts);
            showAlert(INFORMATION, "Saved", "Workouts saved to workouts.csv");
        } catch (Exception e) {
            showAlert(ERROR, "Save Failed", "Could not save workouts to file: " + e.getMessage());
        }
    }

    // "ADD WORKOUT"-BUTTON //
    @FXML
    private void handleAddWorkout() {
        try {
            // Extract fields //
            String type = typeField.getText();
            String notes = notesField.getText();
            LocalDate date = datePicker.getValue();

            // Validate fields //
           if (!validWorkoutData(type, notes, date)) return;
            // Time must be validated before input. NOTE: minutes is divided by 100 for visual representation
            double time = comboBoxHours.getValue() + (comboBoxMinutes.getValue() / 100.0);

            // Create new Workout-object and add to WorkoutTracker //
            Workout newWorkout = new Workout(type, notes, date, time);
            workoutTracker.addWorkout(newWorkout);
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
        for (int minutes = 0; minutes < 60; minutes +=5) {
            comboBoxMinutes.getItems().add(minutes);
        }
    }


    private boolean validWorkoutData(String type, String notes, LocalDate date) {
        if (type == null || type.trim().isEmpty()) {
            showAlert(ERROR, "Invalid Input", "Type must not be empty");
            return false;
        }
        if (date == null) {
            showAlert(ERROR, "Invalid Input", "Date must not be empty");
            return false;
        }
        if (notes == null || notes.trim().length() < 3) {
            showAlert(ERROR, "Invalid Input", "Notes must be at least 3 characters long.");
            return false;
        }
        if (comboBoxHours.getValue() == null || comboBoxMinutes.getValue() == null || comboBoxHours.getValue() + comboBoxMinutes.getValue() == 0) {
            showAlert(ERROR, "Invalid Input", "Please select both hours and minutes.");
            return false;
        }
        return true;
    }

    // ERROR-MESSAGE //
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
