package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import core.Exercise;
import core.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.UiConstants;
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

    // Workout details fields (editable on double-click)
    @FXML private TextField typeField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> comboBoxHours;
    @FXML private ComboBox<Integer> comboBoxMinutes;
    @FXML private TextArea notesArea;

    /** Guards against firing duration commit while programmatically setting values. */
    private boolean suppressDurationCommit = false;

    /** Reference used only for navigation; set by {@link MainController} after loading. */
    private MainController mainController;

    /** The workout currently shown; kept for editing header fields. */
    private Workout workout;

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
        // Sorting doesn't make sense for an index column derived from position.
        setColumn.setSortable(false);

        // Configure double-click-to-edit for header fields
        setupEditableTextField(typeField, () -> {
            String newValue = typeField.getText();
            Workout w = this.workout;
            if (w == null) {
                return;
            }
            try {
                w.setType(newValue);
            } 
            catch (IllegalArgumentException ex) {
                // Revert UI and inform user
                typeField.setText(w.getType());
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Invalid Name", ex.getMessage());
            }
        });

        setupEditableDatePicker(datePicker, () -> {
            LocalDate date = datePicker.getValue();
            Workout w = this.workout;
            if (w == null) {
                return;
            }
            try {
                w.setDate(date);
            } 
            catch (IllegalArgumentException ex) {
                datePicker.setValue(w.getDate());
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Invalid Date", ex.getMessage());
            }
        });

        initializeDurationPickers();

        setupEditableTextArea(notesArea, () -> {
            String newNotes = notesArea.getText();
            Workout w = this.workout;
            if (w == null) {
                return;
            }
            try {
                w.setNotes(newNotes);
            } 
            catch (IllegalArgumentException ex) {
                notesArea.setText(w.getNotes());
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Invalid Notes", ex.getMessage());
            }
        });
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
     * Populates the view from a given {@link Workout}.
     *
     * <p>Copies a snapshot of the workout's exercises into {@link #exerciseItems} and
     * binds the workout header fields (type, date, time, notes) to the workout's current
     * values. Passing {@code null} clears the view.</p>
     *
     * @param workout workout to read exercises and header values from; if {@code null}, the view is cleared
     */
    public void setWorkout(Workout workout) {
        this.workout = workout;
        // Populate header fields
        if (workout != null) {
            typeField.setText(workout.getType());
            datePicker.setValue(workout.getDate());

            int hours = (int) Math.floor(workout.getTime());
            int minutes = (int) Math.round((workout.getTime() - hours) * 100);
            
            suppressDurationCommit = true;
            try {
                comboBoxHours.setValue(hours);
                comboBoxMinutes.setValue(minutes);
            } 
            finally {
                suppressDurationCommit = false;
            }

            notesArea.setText(workout.getNotes());
        } 
        else {
            suppressDurationCommit = true;
            try {
                typeField.clear();
                datePicker.setValue(null);
                comboBoxHours.setValue(null);
                comboBoxMinutes.setValue(null);
                notesArea.clear();
            } 
            finally {
                suppressDurationCommit = false;
            }
        }

        // Populate exercise table from snapshot
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

    // --- Helpers to enable double-click editing with commit on focus loss or Enter ---

    /**
     * Configures a {@link TextField} for "double-click to edit" behavior and commits
     * the change on Enter or when the field loses focus.
     *
     * @param field     the text field to make editable on demand
     * @param onCommit  callback executed after a successful commit (field is set back to non-editable)
     */
    private void setupEditableTextField(TextField field, Runnable onCommit) {
        field.setEditable(false);
        field.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                field.setEditable(true);
                field.requestFocus();
                field.selectAll();
            }
        });
        field.focusedProperty().addListener((obs, oldV, hasFocus) -> {
            if (!hasFocus && field.isEditable()) {
                field.setEditable(false);
                onCommit.run();
            }
        });
        field.setOnAction(e -> {
            if (field.isEditable()) {
                field.setEditable(false);
                onCommit.run();
            }
        });
    }

    /**
     * Configures a {@link TextArea} for "double-click to edit" behavior and commits
     * the change when the area loses focus.
     *
     * @param area      the text area to make editable on demand
     * @param onCommit  callback executed after a successful commit (area is set back to non-editable)
     */
    private void setupEditableTextArea(TextArea area, Runnable onCommit) {
        area.setEditable(false);
        area.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                area.setEditable(true);
                area.requestFocus();
            }
        });
        area.focusedProperty().addListener((obs, oldV, hasFocus) -> {
            if (!hasFocus && area.isEditable()) {
                area.setEditable(false);
                onCommit.run();
            }
        });
    }

    /**
     * Configures a {@link DatePicker} for "double-click to edit" behavior and commits
     * the change on action (e.g., Enter) or when the control loses focus.
     *
     * @param picker    the date picker to make editable on demand
     * @param onCommit  callback executed after a successful commit (picker is set back to non-editable)
     */
    private void setupEditableDatePicker(DatePicker picker, Runnable onCommit) {
        picker.setEditable(false);
        picker.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                picker.setEditable(true);
                picker.requestFocus();
                picker.show();
            }
        });
        picker.focusedProperty().addListener((obs, oldV, hasFocus) -> {
            if (!hasFocus && picker.isEditable()) {
                picker.setEditable(false);
                onCommit.run();
            }
        });
        picker.setOnAction(e -> {
            if (picker.isEditable()) {
                picker.setEditable(false);
                onCommit.run();
            }
        });
    }

    /**
     * Initializes the duration ComboBoxes (hours/minutes).
     *
     * <p>Populates choices based on {@link UiConstants}, keeps both controls enabled so the
     * user can interact directly, and wires listeners that commit changes immediately whenever
     * a value changes or an action occurs.</p>
     */
    private void initializeDurationPickers() {
       // Populate choices
        comboBoxHours.getItems().clear();
        for (int h = 0; h <= UiConstants.MAX_HOURS; h++) {
            comboBoxHours.getItems().add(h);
        }
        comboBoxMinutes.getItems().clear();
        for (int m = 0; m < 60; m += UiConstants.MINUTES_STEP) {
            comboBoxMinutes.getItems().add(m);
        }

        // Always enabled so the user can click the arrows directly
        comboBoxHours.setDisable(false);
        comboBoxMinutes.setDisable(false);

        // Update the workout time any time either value changes
        comboBoxHours.valueProperty().addListener((obs, oldV, newV) -> {
            if (!suppressDurationCommit) {
                commitDuration();
            }
        });
        comboBoxMinutes.valueProperty().addListener((obs, oldV, newV) -> {
            if (!suppressDurationCommit) {
                commitDuration();
            }
        });

        // Also commit on "Action" (e.g., Enter/selection via keyboard)
        comboBoxHours.setOnAction(e -> { 
            if (!suppressDurationCommit) {
                commitDuration();
            } 
        });
        comboBoxMinutes.setOnAction(e -> { 
            if (!suppressDurationCommit) {
                commitDuration();
            } 
        });
    }

    /**
     * Commits the current duration selection to the underlying {@link Workout}.
     *
     * <p>Reads hours/minutes from the ComboBoxes (treating {@code null} as {@code 0})
     * and writes to {@link Workout#setTime(double)} using the {@code h + m/100.0} encoding.
     * If the model rejects the value ({@link IllegalArgumentException}), the UI reverts to
     * the last valid values and an error alert is shown.</p>
     */
    private void commitDuration() {
        try {
            int h = comboBoxHours.getValue() == null ? 0 : comboBoxHours.getValue();
            int m = comboBoxMinutes.getValue() == null ? 0 : comboBoxMinutes.getValue();

            Workout w = this.workout;
            if (w != null) {
                double time = h + (m / 100.0);
                w.setTime(time); // Let Workout validate; we no longer block 0:00 here
            }
        } 
        catch (IllegalArgumentException ex) {
            Workout w = this.workout;
            suppressDurationCommit = true;
            try {
                if (w != null) {
                    int hoursOld = (int) Math.floor(w.getTime());
                    int minutesOld = (int) Math.round((w.getTime() - hoursOld) * 100);
                    comboBoxHours.setValue(hoursOld);
                    comboBoxMinutes.setValue(minutesOld);
                } 
                else {
                    comboBoxHours.setValue(null);
                    comboBoxMinutes.setValue(null);
                }
            } 
            finally {
                suppressDurationCommit = false;
            }
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Invalid Duration", ex.getMessage());
        }
    }
}
