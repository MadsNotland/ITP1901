package ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import static javafx.scene.control.Alert.AlertType.ERROR;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import persistence.WorkoutFileManager;
import ui.base.BaseController;

/**
 * Main controller responsible for coordinating view navigation and shared data management.
 * <p>
 * This controller acts as the central hub of the application, managing transitions between
 * different UI views (e.g., {@link ui.WorkoutTableController} and {@link ui.WorkoutFormController})
 * and maintaining shared references to the {@link core.WorkoutTracker} model and
 * {@link persistence.WorkoutFileManager} used for persistence.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Initialize the main application state on startup</li>
 *   <li>Handle navigation between the workout table and form views</li>
 *   <li>Load and persist workout data to the JSON storage file</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutTableController
 * @see ui.WorkoutFormController
 * @see core.WorkoutTracker
 * @see persistence.WorkoutFileManager
 */
public class MainController extends BaseController {

    // The root layout container for swapping UI views
    @FXML
    private BorderPane contentArea;
    
    // Shared workout tracker instance used across all controllers.
    private final WorkoutTracker workoutTracker = new WorkoutTracker();
    // File manager used for persisting and loading workout data
    private WorkoutFileManager fileManager;

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the application by showing the main table view
     * and attempting to load workout data from file.
     *
     * @throws IOException if an error occurs while loading the initial view
     */
    @FXML
    private void initialize() throws IOException {
        showWorkoutTable();
        loadWorkoutsFromFile();
    }

    /**
     * Returns the shared {@link WorkoutTracker} instance.
     * <p>
     * This instance is shared between all controllers to maintain a consistent
     * state across views.
     * </p>
     *
     * @return the shared workout tracker
     */
    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    /**
     * Loads and displays the {@code WorkoutTableView.fxml} view in the center of the layout.
     * <p>
     * This method also injects the shared {@link WorkoutTracker} and
     * {@link WorkoutFileManager} instances into the {@link WorkoutTableController}.
     * </p>
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
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

    /**
     * Loads and displays the {@code WorkoutFormView.fxml} view in the center of the layout.
     * <p>
     * This method injects the shared {@link WorkoutTracker} instance into the
     * {@link WorkoutFormController} for adding new workouts.
     * </p>
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
    public void showWorkoutForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutFormView.fxml"));
        Region formView = loader.load();

        // Get the controller and send references
        WorkoutFormController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkoutTracker(workoutTracker);

        contentArea.setCenter(formView);
    }


    /**
     * Loads existing workouts from the JSON file on disk into the shared {@link WorkoutTracker}.
     * <p>
     * The method attempts to locate the data file using several common project layouts to ensure
     * compatibility both when running from an IDE and when packaged with Maven.
     * </p>
     * <p>
     * If loading fails, an error dialog is displayed to the user.
     * </p>
     */
    private void loadWorkoutsFromFile() {
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
        } catch (IOException e) {
            showAlert(ERROR, "Load Error", "Could not load workouts from file " + e.getMessage());
        }
    }
}
