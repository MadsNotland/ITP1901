package ui;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

import java.io.IOException;

import core.Workout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import ui.base.BaseController;
import ui.service.WorkoutService;

/**
 * Main controller responsible for coordinating view navigation and shared data management.
 * <p>
 * This controller acts as the central hub of the application, managing transitions between
 * different UI views (e.g., {@link ui.WorkoutTableController} and {@link ui.WorkoutFormController})
 * and maintaining shared references to the {@link ui.service.WorkoutService} model used for persistence.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Initialize the main application state on startup</li>
 *   <li>Handle navigation between the workout table and form views</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutTableController
 * @see ui.WorkoutFormController
 * @see ui.service.WorkoutService
 */
public class MainController extends BaseController {

    // The root layout container for swapping UI views
    @FXML
    private BorderPane contentArea;
    
    // WorkoutService for handling persistence via REST API (WorkoutTracker and RestWorkoutClient)
    private final WorkoutService workoutService = new WorkoutService();

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the application by showing the main table view
     * and attempting to load workout data from the server via REST API.
     *
     * @throws IOException if an error occurs while loading the initial view
     */
    @FXML
    private void initialize() throws IOException {
        showWorkoutTable();
        try {
            workoutService.loadWorkouts();
        } 
        catch (IOException e) {
            showAlert(ERROR, "Server Connection Error", 
                "Could not connect to the server. Please check that the server is running on http://localhost:8080");
        }   
    }

    /**
     * Loads and displays the {@code WorkoutTableView.fxml} view in the center of the layout.
     * <p>
     * This method also injects the shared {@link ui.service.WorkoutService} instance into the {@link WorkoutTableController}.
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
        controller.setWorkoutService(workoutService);
        controller.initializeData();

        contentArea.setCenter(tableView);
    }

    /**
     * Loads and displays the {@code WorkoutFormView.fxml} view in the center of the layout.
     * <p>
     * This method injects the shared {@link ui.service.WorkoutService} instance into the
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
        controller.setWorkoutService(workoutService);

        contentArea.setCenter(formView);
    }

    /**
     * Loads and displays the {@code ExerciseTableView.fxml} view in the center of the layout.
     * <p>
     * This method also sends the Workout that was clicked on to ExerciseTableController
     * </p>
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
    public void showExerciseTable(Workout workout) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseTableView.fxml"));
        Region tableView = loader.load();

        // Get the controller and send references
        ExerciseTableController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkout(workout);

        contentArea.setCenter(tableView);
    }

    public void saveAllWorkoutsWithAlert() {
        try {
            workoutService.saveWorkouts();
            showAlert(INFORMATION, "Saved", "Workouts saved to file.");
        } 
        catch (IOException e) {
            showAlert(ERROR, "Save Error", "Could not save workouts: " + e.getMessage());
        }
    }

    public Workout findWorkoutById(Long id) {
        if (id == null) {
            return null;
        }
        return workoutService.getWorkoutTracker()
                .getAllWorkouts()
                .stream()
                .filter(w -> id.equals(w.getId()))
                .findFirst()
                .orElse(null);
    }
}
