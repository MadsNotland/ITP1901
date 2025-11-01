package ui;

import java.io.IOException;
import java.util.List;

import core.Exercise;
import core.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ui.base.BaseController;


/**
 * Controller for {@code ExerciseTableView.fxml}.
 *
 * <p>Shows the list of {@link Exercise} items that belong to a given {@link Workout}.
 * This controller is intentionally <b>read-only</b>: it receives a Workout and
 * copies its exercises into an internal ObservableList (a snapshot) rather than
 * holding a reference to the Workout. </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Bind Exercise properties to table columns</li>
 *   <li>Compute and display a 1-based "Set #" column based on row index</li>
 *   <li>Support navigation back to the workout table via {@link MainController}</li>
 * </ul>
 */
public class ExerciseTableController extends BaseController {

    /** Table showing the exercises snapshot. */
    @FXML private TableView<Exercise> exerciseTable;

    @FXML private TableColumn<Exercise, String>  nameColumn;
    @FXML private TableColumn<Exercise, Integer> repsColumn;
    @FXML private TableColumn<Exercise, Double>  weightColumn;

    /**
     * Computed "Set #" column—shows a 1-based index for each visible row.
     * This is not a model property; it is derived from the row position.
     */
    @FXML private TableColumn<Exercise, Number>  setColumn;

    /** Reference used only for navigation; set by {@link MainController} after loading. */
    private MainController mainController;

    /**
     * Items displayed in the table. We populate this list from {@link #setWorkout(Workout)}
     * using a snapshot of the Workout's exercises, so later edits elsewhere won't mutate
     * what's shown here unless {@code setWorkout(...)} is called again.
     */
    private final ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    /**
     * Called automatically by the FXMLLoader after FXML fields are injected.
     * Wires up column bindings and connects the table to {@link #exerciseItems}.
     */
    @FXML
    private void initialize() {
        // Bind columns to Exercise bean properties (via JavaFX PropertyValueFactory reflection).
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("repetitions"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // Show the observable items in the table.
        exerciseTable.setItems(exerciseItems);

        // Compute "Set #" as 1-based row index; use current items to look up the item's position.
        setColumn.setCellValueFactory(cd ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(
                exerciseTable.getItems().indexOf(cd.getValue()) + 1
            )
        );
        setColumn.setSortable(false);

        exerciseTable.setEditable(true);

       // Name
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(this::onNameEditCommit);

        // Reps
        repsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        repsColumn.setOnEditCommit(this::onRepsEditCommit);

        // Weight
        weightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        weightColumn.setOnEditCommit(this::onWeightEditCommit);
    }

    /**
     * Injects the parent controller for navigation.
     * Must be called by {@link MainController} after loading this controller.
     *
     * @param mainController parent controller used to navigate back
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Populates the table with a snapshot of the given workout's exercises.
     * <p><b>Important:</b> This method does not store the {@code Workout} reference,
     * only copies its current exercises into {@link #exerciseItems}.</p>
     *
     * @param workout workout to read exercises from; if {@code null}, the table is cleared
     */
    public void setWorkout(Workout workout) {
        // Workout#getExercises() already returns a defensive copy in your model.
        List<Exercise> snapshot = (workout == null) ? List.of() : workout.getExercises();
        exerciseItems.setAll(snapshot);
    }

    /**
     * Handles the Back button: navigates to the workout table view.
     *
     * @throws IOException if the destination view fails to load
     */
    @FXML
    private void handleBack() throws IOException {
        // Assumes MainController was injected; if not, this will NPE—caller must set it.
        mainController.showWorkoutTable();
    }

    private void onNameEditCommit(TableColumn.CellEditEvent<Exercise, String> evt) {
        String newVal = (evt.getNewValue() == null) ? "" : evt.getNewValue().trim();
        if (!newVal.isBlank()) {
            evt.getRowValue().setName(newVal);
        }
        else {
            exerciseTable.refresh();
        }
    }

    private void onRepsEditCommit(TableColumn.CellEditEvent<Exercise, Integer> evt) {
        Integer v = evt.getNewValue();
        if (v != null && v > 0) {
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
}
