package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import core.Exercise;
import core.Workout;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
 *   <li>Header edits (type/date/duration/notes) are staged in controller properties and do not modify the underlying {@link Workout}.</li>
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

    /**
     * Items displayed in the table. We populate this list from {@link #setWorkout(Workout)}
     * using a snapshot of the Workout's exercises, so later edits elsewhere won't mutate
     * what's shown here unless {@code setWorkout(...)} is called again.
     */
    private final ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    /** Reference used only for navigation; set by {@link MainController} after loading. */
    private MainController mainController;

    /** The workout currently shown; kept for editing header fields. */
    private Workout workout;

    /** Staged edits for header fields before committing to the Workout. */
    private final StringProperty stagedType   = new SimpleStringProperty("");
    private final StringProperty stagedNotes  = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> stagedDate = new SimpleObjectProperty<>(null);
    private final IntegerProperty stagedHours   = new SimpleIntegerProperty(0);
    private final IntegerProperty stagedMinutes = new SimpleIntegerProperty(0);

    /** Guards against programmatic ComboBox changes firing listeners */
    private boolean suppressDurationListeners = false;

    /**
     * Called automatically by the FXMLLoader after FXML fields are injected.
     * Wires up column bindings, connects the table to {@link #exerciseItems},
     * and binds header controls to staged properties.
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

        // Bind staged properties to the header fields
        typeField.textProperty().bindBidirectional(stagedType);
        notesArea.textProperty().bindBidirectional(stagedNotes);
        datePicker.valueProperty().bindBidirectional(stagedDate);

        // Sorting doesn't make sense for an index column derived from position.
        setColumn.setSortable(false);

        /*Duration pickers: populate and wire to stagedHours/Minutes */
        initializeDurationPickers();

        // Configure double-click-to-edit for header fields
        setupEditableTextField(typeField);
        setupEditableDatePicker(datePicker);
        setupEditableTextArea(notesArea);
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
     * seeds the staged header properties (type, date, time, notes) from the workout.
     * Passing {@code null} clears the view and resets staged values.</p>
     *
     * @param workout workout to read exercises and header values from; if {@code null}, the view is cleared
     */
    public void setWorkout(Workout workout) {
        if (this.workout == workout) {
            return;
        }
        this.workout = workout;

        // Populate exercise table from snapshot
        List<Exercise> snapshot = (workout == null) ? List.of() : workout.getExercises();
        exerciseItems.setAll(snapshot);

        // Populate header fields
        if (workout != null) {
            stagedType.set(workout.getType());
            stagedDate.set(workout.getDate());
            stagedNotes.set(workout.getNotes());

            int hours = (int) Math.floor(workout.getTime());
            int minutes = (int) Math.round((workout.getTime() - hours) * 100);
            
            suppressDurationListeners = true;
            try {
                stagedHours.set(hours);
                stagedMinutes.set(minutes);
                comboBoxHours.setValue(hours);
                comboBoxMinutes.setValue(minutes);
            } 
            finally {
                suppressDurationListeners = false;
            }
        } 
        else {
            stagedType.set("");
            stagedNotes.set("");
            stagedDate.set(null);

            suppressDurationListeners = true;
            try {
                stagedHours.set(0);
                stagedMinutes.set(0);
                comboBoxHours.setValue(null);
                comboBoxMinutes.setValue(null);
            } 
            finally {
                suppressDurationListeners = false;
            }
        }
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

    // --- Helpers to enable double-click editing ---

    /**
     * Enables "double-click to edit" for a {@link TextField}.
     * Toggles the field editable while focused and reverts to non-editable on blur/Enter.
     * Does not write to the model; only updates the staged property bound to the control.
     *
     * @param field     the text field to make editable on demand (back to non-editable on blur)
     */
    private void setupEditableTextField(TextField field) {
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
            }
        });
        field.setOnAction(e -> {
            if (field.isEditable()) {
                field.setEditable(false);
            }
        });
    }

    /**
     * Enables "double-click to edit" for a {@link TextArea}.
     * Toggles the field editable while focused and reverts to non-editable on blur/Enter.
     * Does not write to the model; only updates the staged property bound to the control.
     *
     * @param area      the text area to make editable on demand (back to non-editable on blur)
     */
    private void setupEditableTextArea(TextArea area) {
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
            }
        });
    }

    /**
     * Enables "double-click to edit" for a {@link DatePicker}.
     * Toggles the field editable while focused and reverts to non-editable on blur/Enter.
     * Does not write to the model; only updates the staged property bound to the control.
     *
     * @param picker    the date picker to make editable on demand (back to non-editable on blur)
     */
    private void setupEditableDatePicker(DatePicker picker) {
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
            }
        });
        picker.setOnAction(e -> {
            if (picker.isEditable()) {
                picker.setEditable(false);
            }
        });
    }

    /**
     * Initializes the duration ComboBoxes (hours/minutes).
     *
     * <p>Populates choices based on {@link UiConstants}, leaves controls enabled,
     * and wires listeners so user changes update {@link #stagedHours} / {@link #stagedMinutes} only.
     * Programmatic changes are guarded by {@link #suppressDurationListeners} to avoid feedback loops.</p>
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

        // When user changes a value, only update staged properties
        comboBoxHours.valueProperty().addListener((obs, oldV, newV) -> {
            if (suppressDurationListeners) {
                return;
            }
            stagedHours.set(newV == null ? 0 : newV);
        });
        comboBoxMinutes.valueProperty().addListener((obs, oldV, newV) -> {
            if (suppressDurationListeners) {
                return;
            }
            stagedMinutes.set(newV == null ? 0 : newV);
        });

        comboBoxHours.setOnAction(e -> { 
            if (suppressDurationListeners) {
                return;
            }
            Integer v = comboBoxHours.getValue();
            stagedHours.set(v == null ? 0 : v);
        });
        comboBoxMinutes.setOnAction(e -> { 
            if (suppressDurationListeners) {
                return;
            }
            Integer v = comboBoxMinutes.getValue();
            stagedMinutes.set(v == null ? 0 : v);
        });
    }
}
