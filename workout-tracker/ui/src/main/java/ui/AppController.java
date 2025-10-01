package ui;

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
import persistence.WorkoutFileManager;

/**
 * Controller for the Workout Tracker application.
 * Handles user interactions and manages workout data persistence.
 */
public class AppController {

    // Input components
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

    // Table view components
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

    // Core business logic components
    private WorkoutTracker workoutTracker;
    private WorkoutFileManager fileManager;


    @FXML
    void initialize() {
        initializeComboBoxes();
        workoutTracker = new WorkoutTracker();
        setTableView();


        // Load workouts from file
        String currentDir = System.getProperty("user.dir"); // Set relative filepath, so it will work for both mvn build and IDE
        if (currentDir == null) throw new IllegalStateException("Cannot determine working directory");

        Path cwd = Paths.get(currentDir).toAbsolutePath();
        // Primary: when running from module dir: workout-tracker/ui -> ../data/workouts.csv
        Path candidate = cwd.getParent() != null ? cwd.getParent().resolve("data").resolve("workouts.json") : cwd.resolve("data").resolve("workouts.json");
        // Fallback: when running from project root: workout-tracker -> data/workouts.csv
        if (!Files.exists(candidate)) {
            Path projectRootCandidate = cwd.resolve("data").resolve("workouts.json");
            if (Files.exists(projectRootCandidate)) {
                candidate = projectRootCandidate;
            }
        }
        // Last resort: repository layout from older structure if present
        if (!Files.exists(candidate)) {
            Path legacy = cwd.resolve("app").resolve("data").resolve("workouts.json");
            if (Files.exists(legacy)) {
                candidate = legacy;
            }
        }
        Path workoutFilePath = candidate;

        fileManager = new WorkoutFileManager(workoutFilePath);
        try {
            var loaded = fileManager.loadWorkouts();
            workoutTracker.getAllWorkouts().setAll(loaded);
        } catch (Exception e) {
            showAlert(ERROR, "Load Error", "Could not load workouts from file " + e.getMessage());
        }
    }

    /**
     * Handles the save button click event.
     * Saves all workouts to the CSV file.
     */
    @FXML
    private void handleSave() {
        List<Workout> workouts = new ArrayList<>(workoutTracker.getAllWorkouts());
        try {
            fileManager.saveWorkouts(workouts);
            showAlert(INFORMATION, "Saved", "Workouts saved to workouts.json");
        } catch (Exception e) {
            showAlert(ERROR, "Save Failed", "Could not save workouts to file: " + e.getMessage());
        }
    }

    /**
     * Handles the add workout button click event.
     * Validates input and adds a new workout to the tracker.
     */
    @FXML
    private void handleAddWorkout() {
        try {
            // Extract input fields
            String type = typeField.getText();
            String notes = notesField.getText();
            LocalDate date = datePicker.getValue();

            // Validate input fields
            if (!validWorkoutData(type, notes, date)) return;
            // Time must be validated before input. NOTE: minutes is divided by 100 for visual representation
            double time = comboBoxHours.getValue() + (comboBoxMinutes.getValue() / 100.0);

            // Create and add new workout
            Workout newWorkout = new Workout(type, notes, date, time);
            workoutTracker.addWorkout(newWorkout);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the table view with workout data and column mappings.
     */
    private void setTableView() {
        // Connect table view to workout data
        workoutTable.setItems(workoutTracker.getAllWorkouts()); 

        // Map table columns to workout properties
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }


    /**
     * Initializes the time selection combo boxes with available values.
     */
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


    /**
     * Validates workout input data and shows error messages if validation fails.
     * @param type the workout type
     * @param notes the workout notes
     * @param date the workout date
     * @return true if all validation passes, false otherwise
     */
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

    /**
     * Shows an alert dialog with the specified type, title and message.
     * @param type the alert type (ERROR, INFORMATION, etc.)
     * @param title the alert title
     * @param message the alert message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
