package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import persistence.WorkoutFileManager;
import ui.base.BaseController;

/**
 * Controller class for the {@code WorkoutTableView.fxml} view.
 * <p>
 * Displays all recorded workouts in a table and provides controls
 * for adding and saving workouts. This view serves as the main 
 * overview screen of the application.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Bind {@link core.Workout} data to the table columns</li>
 *   <li>Delegate navigation to {@link MainController}</li>
 *   <li>Persist workout data via {@link persistence.WorkoutFileManager}</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutFormController
 * @see core.Workout
 * @see core.WorkoutTracker
 */
public class WorkoutTableController extends BaseController {

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

    // Core dependencies (injected by MainController)
    private WorkoutTracker workoutTracker;
    private WorkoutFileManager fileManager;
    private MainController mainController;

    /**
     * Initializes the table view once dependencies have been injected.
     * Called manually by {@link MainController} after FXML loading.
     */
    @FXML
    void initializeData() {
        setTableView();
    }


    /**
     * Injects the {@link MainController} to enable view navigation.
     *
     * @param mainController reference to the parent {@link MainController}
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Injects the shared {@link WorkoutTracker} instance containing
     * all workouts managed by the application.
     *
     * @param workoutTracker the shared workout tracker
     */
    public void setWorkoutTracker(WorkoutTracker workoutTracker) {
        this.workoutTracker = workoutTracker;
    }

    /**
     * Injects the {@link WorkoutFileManager} responsible for reading and writing
     * workout data to a persistent JSON file.
     *
     * @param fileManager the file manager instance used for persistence
     */
    public void setFileManager(WorkoutFileManager fileManager) {
        this.fileManager = fileManager;
    }
    

    /**
     * Navigates to the workout form view when the user presses the "Add" button.
     *
     * @throws IOException if the form view cannot be loaded
     */
    @FXML
    private void handleAddButton() throws IOException {
        mainController.showWorkoutForm();
    }


    /**
     * Saves all current workouts to file when the "Save" button is pressed.
     * Displays a confirmation or error alert depending on the result.
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
     * Binds table columns to {@link Workout} properties and connects
     * the table to the observable list of workouts.
     */
    private void setTableView() {
        workoutTable.setItems(workoutTracker.getAllWorkouts());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }
}
