package ui;

import java.io.IOException;

import core.Exercise;
import core.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.base.BaseController;

/**
 * Controller class for the {@code ExerciseTableView.fxml} view.
 * <p>
 * Displays all exercises belonging to a selected {@link core.Workout}.
 * Provides controls for adding, editing, and saving exercises.
 * </p>
 *
 * <p><b>Responsibilities</b></p>
 * <ul>
 *   <li>Bind {@link core.Exercise} data to the table columns</li>
 *   <li>Navigate to exercise form / back to workouts via {@link MainController}</li>
 *   <li>Persist data via {@link ui.service.WorkoutService}</li>
 * </ul>
 *
 * @see ui.ExerciseFormController
 * @see core.Exercise
 * @see core.Workout
 * @see ui.service.WorkoutService
 */
public class ExerciseTableController extends BaseController {

    // Table view components
    @FXML
    private TableView<Exercise> exerciseTable;
    @FXML
    private TableColumn<Exercise, String> nameColumn;
    @FXML
    private TableColumn<Exercise, Integer> repsColumn;
    @FXML
    private TableColumn<Exercise, Double> weightColumn;
    @FXML
    private TableColumn<Exercise, Number> setColumn;

    // Core dependencies (injected by MainController)
    private MainController mainController;

    // The workout whose exercises we display
    private Workout currentWorkout;

    // Backing list for the table; we refresh from currentWorkout when needed
    private final ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    /**
     * Initializes column bindings. Call {@link #initializeData()} after dependencies are set.
     */
    @FXML
    private void initialize() {
        // Bind columns to Exercise properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("repetitions"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        exerciseTable.setItems(exerciseItems);

        setColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(
                exerciseTable.getItems().indexOf(cellData.getValue()) + 1
            )
        );
    }

    /**
     * Called by {@link MainController} after it sets dependencies and the current workout.
     * Ensures the table reflects the latest exercises for {@link #currentWorkout}.
     */
    @FXML
    public void initializeData() {
        refreshTable();
    }

    /** Injects the {@link MainController} to enable view navigation. */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // /** Sets which workout's exercises to display. Must be called before {@link #initializeData()}. */
    public void setWorkout(Workout workout) {
        this.currentWorkout = workout;
    }

    /**
     * Refreshes the visible table items from the underlying current workout.
     */
    public void refreshTable() {
        exerciseItems.setAll(currentWorkout != null ? currentWorkout.getExercises() : FXCollections.observableArrayList());
    }

    @FXML
    private void handleBack() throws IOException {
        mainController.showWorkoutTable();
    }
}
