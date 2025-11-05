package ui;
import java.io.IOException;
import java.time.LocalDate;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    @FXML 
    private Button deleteWorkoutButton;
    @FXML
    private javafx.scene.layout.StackPane deleteButtonSlot;

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
        setupWorkoutTableSelectionListener();

        // Show/hide the side delete button when a row is selected
        deleteWorkoutButton.visibleProperty().bind(
            workoutTable.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0)
        );
        deleteButtonSlot.setManaged(true);
        deleteWorkoutButton.setManaged(true);
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
     * Injects the shared {@link WorkoutService} instance used to store and manage
     * all workouts in the application via the {@link WorkoutTracker}.
     * <p>
     * This reference is provided by the {@link MainController} after FXML loading,
     * ensuring that all controllers operate on the same underlying data model.
     * </p>
     * 
     * @param workoutService
     */
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
     * Loads workouts from the server via {@link WorkoutService}
     * and handles any potential IO exceptions.
     */
    @FXML
    private void handleRowClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Workout selected = workoutTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                mainController.showExerciseTable(selected);
            }
        }
    }

    /* Deletes the selected workout after user confirmation */
    @FXML
    private void handleDeleteWorkout(){
        Workout selected = workoutTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (selected.getId() == null){
            showAlert(Alert.AlertType.WARNING,
                "Cannot delete",
                "This workout hasn’t been saved to the server yet, so it has no ID.\n" +
                "Save it first, then try deleting.");
            return;
        }

        boolean confirmed = confirm(
            "Delete workout",
            "Are you sure you want to delete this workout?",
            "Type: %s%nDate: %s%nTime: %s%n%nThis cannot be undone."
                .formatted(selected.getType(), selected.getDate(), selected.getTime())
        );
        if(!confirmed){
            return;
        }
        try {
            workoutService.deleteWorkout(selected);
        } 
        catch (IllegalArgumentException iae) {
            showAlert(
                Alert.AlertType.WARNING,
                "Delete failed",
                iae.getMessage()
            );
        }
        catch (IOException ioe) {
            showAlert(
                Alert.AlertType.ERROR,
                "Network Error",
                "Could not delete workout from server.\n\n" + ioe.getMessage()
            );
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

    /**
     * Sets up a listener for user interactions with the workout table.
     * <p>
     * This listener detects when the user clicks outside the {@link #workoutTable}
     * and clears any existing selection. It improves the overall user experience
     * by preventing a row from remaining highlighted when the user interacts with
     * other parts of the view.
     * </p>
     * <p>
     * The listener is attached dynamically once the table’s {@link javafx.scene.Scene}
     * becomes available, ensuring proper event handling after the FXML view is loaded.
     * </p>
     */
    private void setupWorkoutTableSelectionListener() {
        workoutTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                    javafx.scene.Node node = (javafx.scene.Node) e.getTarget();
                    boolean inside = false;
                    while (node != null) {
                        if (node == workoutTable || node == deleteWorkoutButton || node == deleteButtonSlot) {
                            inside = true;
                            break;
                        }
                        node = node.getParent();
                    }
                    if (!inside) {
                        workoutTable.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    /* Simple Message-Display for Info-Button */
    @FXML
    private void handleInfo() {
        String infoText = """
                     Workout Tracker App Guide
            
            • Click "Add Workout" button to add a workout.
            • Fill in necessary input-fields and add workout.
            • Each workout is saved automatically.
            • Doubleclick a workout-row to view its exercises.
            • Click outside the table to clear a selection.
            """;

        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
            "About Workout Tracker", infoText);
    }

    /* Helper for confirm box for deleting exercises */
    private boolean confirm(String title, String header, String content) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        var result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }
}
