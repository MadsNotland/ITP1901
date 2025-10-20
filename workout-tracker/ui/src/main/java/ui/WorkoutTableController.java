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


    /**
     * JavaFX initialization hook. Called automatically by the FXMLLoader after
     * the FXML elements have been injected. Prepares the UI widgets, creates the
     * {@link WorkoutTracker}, binds the table view, and attempts to load data
     * from a JSON file.
     */
    @FXML
    void initialize() {
        workoutTracker = new WorkoutTracker();
        setTableView();

        // Load workouts from file

        // Resolve a JSON path relative to the current working directory so it
        // works both from an IDE and when built via Maven.
        String currentDir = System.getProperty("user.dir");
        if (currentDir == null) {
            throw new IllegalStateException("Cannot determine working directory");
        }
        
        Path cwd = Paths.get(currentDir).toAbsolutePath();
        // Primary: running from the UI module directory: workout-tracker/ui -> ../data/workouts.json
        Path cwdParent = cwd.getParent();
        Path candidate = cwdParent != null ? cwdParent.resolve("data").resolve("workouts.json") : cwd.resolve("data").resolve("workouts.json");
        // Fallback: running from project root: workout-tracker -> data/workouts.json
        if (!Files.exists(candidate)) {
            Path projectRootCandidate = cwd.resolve("data").resolve("workouts.json");
            if (Files.exists(projectRootCandidate)) {
                candidate = projectRootCandidate;
            }
        }
        // Last resort: legacy repository layout if present
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

