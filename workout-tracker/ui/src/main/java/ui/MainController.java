package ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.ERROR;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import persistence.WorkoutFileManager;

public class MainController {

    @FXML
    private BorderPane contentArea;
    
    // One common WorkoutTracker for the entire application
    private final WorkoutTracker workoutTracker = new WorkoutTracker();
    private WorkoutFileManager fileManager;


    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    @FXML
    private void initialize() throws IOException {
        showWorkoutTable();
        loadWorkoutsFromFile();
    }

    public void showWorkoutTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutTableView.fxml"));
        Region tableView = loader.load();

        // Get the controller and send references
        WorkoutTableController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkoutTracker(workoutTracker);
        controller.setFileManager(fileManager);
        controller.initializeData();

        contentArea.setCenter(tableView);
    }

    public void showWorkoutForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutFormView.fxml"));
        Region formView = loader.load();

        // Get the controller and send references
        WorkoutFormController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkoutTracker(workoutTracker);

        contentArea.setCenter(formView);
    }

    private void loadWorkoutsFromFile() {
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
