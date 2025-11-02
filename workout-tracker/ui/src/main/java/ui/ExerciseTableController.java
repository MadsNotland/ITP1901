package ui;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.Exercise;
import core.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ui.base.BaseController;
import ui.service.WorkoutService;

/**
 * Controller for {@code ExerciseTableView.fxml}.
 *
 * Editing uses a deep snapshot:
 * - On load, we CLONE each Exercise (preserving id) into an internal ObservableList.
 * - Add/Delete/Edit affect ONLY these clones.
 * - "Save" merges the edited clones back into the bound Workout.
 * - "Back" navigates away and DISCARDs edits (no merge).
 */
public class ExerciseTableController extends BaseController {

    @FXML private TableView<Exercise> exerciseTable;

    @FXML private TableColumn<Exercise, String>  nameColumn;
    @FXML private TableColumn<Exercise, Integer> repsColumn;
    @FXML private TableColumn<Exercise, Double>  weightColumn;
    @FXML private TableColumn<Exercise, Number>  setColumn;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;

    /** We keep only the immutable identifier to avoid SpotBugs EI_EXPOSE_REP2. */
    private Long workoutId;

    /** Deep-snapshot (cloned) items shown/edited in the table. */
    private final ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    /** Injected by MainController for navigation & model lookup. */
    private MainController mainController;
    private WorkoutService workoutService;

    @FXML
    private void initialize() {
        // Bind columns to bean properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("repetitions"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        exerciseTable.setItems(exerciseItems);

        // Computed 1-based index column, not sortable
        setColumn.setCellValueFactory(cd ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(
                exerciseTable.getItems().indexOf(cd.getValue()) + 1
            )
        );
        setColumn.setSortable(false);

        // Inline editing
        exerciseTable.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(this::onNameEditCommit);

        repsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        repsColumn.setOnEditCommit(this::onRepsEditCommit);

        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        weightColumn.setOnEditCommit(this::onWeightEditCommit);

        // Selection & toolbar state
        exerciseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (deleteButton != null) {
            deleteButton.disableProperty().bind(
                exerciseTable.getSelectionModel().selectedItemProperty().isNull()
            );
        }
    }

    /** Provided by MainController after loading. */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setWorkoutService(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    /**
     * Load a workout into the editor: deep-copy each Exercise to isolate edits.
     */
    public void setWorkout(Workout workout) {
        this.workoutId = (workout == null) ? null : workout.getId();
        exerciseItems.clear();
        if (workout == null) {
            return;
        }
        for (Exercise e : workout.getExercises()) {
            // Clone: keep id so we can merge by identity
            exerciseItems.add(new Exercise(e.getName(), e.getRepetitions(), e.getWeight(), e.getId()));
        }
    }

    /** Back = discard edits and go to workout table. */
    @FXML
    private void handleBack() throws IOException {
        mainController.showWorkoutTable();
    }

    /** Save = merge deep snapshot into the model (memory only), then stay here or navigate—your call. */
    @FXML
    private void handleSave() {
        Workout w = findWorkout();
        if (w == null) {
            return;
        }
        // Build a fresh list of copies (immutable to the UI list)
        List<Exercise> updated = new ArrayList<>(exerciseItems.size());
        for (Exercise e : exerciseItems) {
            updated.add(new Exercise(e.getName(), e.getRepetitions(), e.getWeight(), e.getId()));
        }
        // Replace model list (see Workout#setExercises below)
        w.setExercises(updated);

        try {
            workoutService.saveWorkouts();
            showAlert(INFORMATION, "Saved", "Workouts saved to workouts.json");
        } 
        catch (Exception e) {
            showAlert(ERROR, "Save Failed", "Could not save workouts to file: " + e.getMessage());
        }
    }

    /** Add a new CLONED exercise to the editor only (not the model until Save). */
    @FXML
    private void handleAdd() {
        Exercise ex = new Exercise("New exercise", 8, 0.0, null); // id auto-UUID
        exerciseItems.add(ex);

        int row = exerciseItems.size() - 1;
        exerciseTable.getSelectionModel().clearAndSelect(row, nameColumn);
        exerciseTable.scrollTo(row);
        exerciseTable.edit(row, nameColumn);
    }

    /** Delete selection from editor only (not the model until Save). */
    @FXML
    private void handleDelete() {
        var selected = new ArrayList<>(exerciseTable.getSelectionModel().getSelectedItems());
        if (!selected.isEmpty()) {
            exerciseItems.removeAll(selected);
        }
    }

    // ---- edit commit handlers ----
    private void onNameEditCommit(TableColumn.CellEditEvent<Exercise, String> evt) {
        String v = (evt.getNewValue() == null) ? "" : evt.getNewValue().trim();
        if (!v.isBlank()) {
            evt.getRowValue().setName(v);
        }
        else {
            exerciseTable.refresh();
        }
    }

    private void onRepsEditCommit(TableColumn.CellEditEvent<Exercise, Integer> evt) {
        Integer v = evt.getNewValue();
        if (v != null && v >= 0) {
            evt.getRowValue().setRepetitions(v);
        }
        else {
            exerciseTable.refresh();
        }
    }

    private void onWeightEditCommit(TableColumn.CellEditEvent<Exercise, Double> evt) {
        Double v = evt.getNewValue();
        if (v != null && v >= 0.0) {
            evt.getRowValue().setWeight(v);
        }
        else {
            exerciseTable.refresh();
        }
    }

    // ---- helpers ----
    private Workout findWorkout() {
        if (mainController == null || workoutId == null) {
            System.out.println("mainController == null || workoutId == null");
            return null;
        }
        return mainController.findWorkoutById(workoutId);
    }
}
