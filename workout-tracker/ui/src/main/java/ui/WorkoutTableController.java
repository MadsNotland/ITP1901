package ui;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

import java.io.IOException;
import java.time.LocalDate;

import core.Workout;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import ui.base.BaseController;
import ui.service.WorkoutService;

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
 *   <li>Persist workout data via {@link ui.service.WorkoutService}</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutFormController
 * @see core.Workout
 * @see ui.service.WorkoutService
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
    private MainController mainController;
    private WorkoutService workoutService;

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

    public void setWorkoutService(WorkoutService workoutService) {
        this.workoutService = workoutService;
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
        try {
            workoutService.saveWorkouts();
            showAlert(INFORMATION, "Saved", "Workouts saved to workouts.json");
        } 
        catch (Exception e) {
            showAlert(ERROR, "Save Failed", "Could not save workouts to file: " + e.getMessage());
        }
    }

    @FXML
    private void handleRowClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Workout selected = workoutTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                mainController.showExerciseTable(selected);
            }
        }
    }

    /**
     * Binds table columns to {@link Workout} properties and connects
     * the table to the observable list of workouts.
     */
    private void setTableView() {
        workoutTable.setItems(workoutService.getWorkoutTracker().getAllWorkouts());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }
}
