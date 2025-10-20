package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import persistence.WorkoutFileManager;

/**
 * Controller for the Workout Tracker JavaFX application.
 * <p>
 * Responsibilities:
 * - Read user input from UI controls
 * - Validate input before creating {@link core.Workout} instances
 * - Keep the table view in sync with the underlying {@link core.WorkoutTracker}
 * - Load and save workout data using {@link persistence.WorkoutFileManager}
 * </p>
 */
public class WorkoutTableController {

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
    private MainController mainController;

    // Control management for workoutTracker object
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setWorkoutTracker(WorkoutTracker workoutTracker) {
        this.workoutTracker = workoutTracker;
    }

    public void setFileManager(WorkoutFileManager fileManager) {
        this.fileManager = fileManager;
    }


    /**
     * JavaFX initialization hook. Called automatically by the FXMLLoader after
     * the FXML elements have been injected. Prepares the UI widgets, creates the
     * {@link WorkoutTracker}, binds the table view, and attempts to load data
     * from a JSON file.
     */
    @FXML
    void initializeData() {
        setTableView();
    }


    @FXML
    private void handleAddButton() throws IOException {
        mainController.showWorkoutForm();
    }


    /**
     * Handles the Save button click.
     * Attempts to persist all workouts to the configured JSON file and shows a
     * confirmation or error dialog.
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
     * Connects the table view to the observable workout list and binds columns
     * to the corresponding {@link Workout} properties.
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
     * Shows a modal alert dialog.
     *
     * @param type    alert type (e.g., ERROR, INFORMATION)
     * @param title   alert title
     * @param message alert message body
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

