package ui;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Exercise;
import core.Workout;
import core.WorkoutTracker;
import core.service.WorkoutService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ui.base.BaseController;

/**
 * Controller for {@code ExerciseTableView.fxml}.
 *
 * Editing uses a deep snapshot:
 * - On load, we CLONE each Exercise (preserving id) into an internal ObservableList.
 * - Add/Delete/Edit affect ONLY these clones.
 * - "Save" merges the edited clones back into the bound Workout.
 * - "Back" navigates away and DISCARDs edits (no merge).
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

    @FXML private TableView<Exercise> exerciseTable;

    @FXML private TableColumn<Exercise, String>  nameColumn;
    @FXML private TableColumn<Exercise, Integer> repsColumn;
    @FXML private TableColumn<Exercise, Double>  weightColumn;
    @FXML private TableColumn<Exercise, Number>  setColumn;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    // Workout details fields (editable on double-click)
    @FXML private TextField typeField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Integer> comboBoxHours;
    @FXML private ComboBox<Integer> comboBoxMinutes;
    @FXML private TextArea notesArea;

    /** We keep only the immutable identifier to avoid SpotBugs EI_EXPOSE_REP2. */
    private Long workoutId;

    /** Deep-snapshot (cloned) items shown/edited in the table. */
    private final ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    /** Reference used only for navigation; set by {@link MainController} after loading. */
    private MainController mainController;

    private WorkoutService workoutService;

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
        initializeColumns();
        initializeSelectionAndEditing();
        initializeHeaderBindings();
        initializeDurationPickers();

        // Configure double-click-to-edit for header fields
        setupEditableTextField(typeField);
        setupEditableDatePicker(datePicker);
        setupEditableTextArea(notesArea);

        installRowReorderDnD();
    }

    /** Provided by MainController after loading. */
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
     * @param workoutService the shared {@link WorkoutService} instance
     */
    public void setWorkoutService(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    /* Populates the view from a given {@link Workout}.
     *
     * <p>Copies a snapshot of the workout's exercises into {@link #exerciseItems} and
     * seeds the staged header properties (type, date, time, notes) from the workout.
     * Passing {@code null} clears the view and resets staged values.</p>
     *
     * @param workout workout to read exercises and header values from; if {@code null}, the view is cleared
     */

    public void setWorkout(Workout workout) {
        exerciseItems.clear();
        this.workoutId = (workout == null) ? null : workout.getId();

        if (workout == null) {
            clearWorkoutView();
        }
        else {
            populateWorkoutView(workout);
        }
    }
    
    /**
     * Populates the UI fields and exercise table with data from the given workout.
     * <p>
     * Creates deep copies of exercises, updates staged header values, and synchronizes
     * duration ComboBoxes with hours and minutes.
     * </p>
     *
     * @param workout the workout whose data will be displayed
     */
    private void populateWorkoutView(Workout workout) {
        // Clone exercises so edits don't affect the original until saved
        for (Exercise e : workout.getExercises()) {
            // Clone: keep id so we can merge by identity
            exerciseItems.add(new Exercise(e.getName(), 
                                e.getRepetitions(), e.getWeight(), e.getId()));
        }

        // Copy header values
        stagedType.set(workout.getType());
        stagedDate.set(workout.getDate());
        stagedNotes.set(workout.getNotes());
        // Update hours/minutes pickers from workout time
        updateDurationPickers(workout);
    }

    /**
     * Updates the staged hour/minute values and ComboBoxes based on the workout time.
     * <p>
     * Uses the suppress flag to prevent unwanted listener triggers during programmatic changes.
     * </p>
     */
    private void updateDurationPickers(Workout workout) {
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

    /**
     * Clears all staged values and resets the UI when no workout is selected.
     * <p>
     * Resets the staged values and resets the duration ComboBoxes.
     * </p>
     */
    private void clearWorkoutView() {
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

    /**
     * Discards the edits and goes to the workout table.
     * <p>
     * Navigates back to the workout table.
     * </p>
     */
    @FXML
    private void handleBack() throws IOException {
        mainController.showWorkoutTable();
    }

    /** Save = merge deep snapshot into the model (memory only), then stay here or navigate—your call.
     * <p>
     * Merges the deep snapshot into the model and saves it to the file.
     * </p>
     */
    @FXML
    private void handleSave() {
        Workout w = workoutService.findWorkout(this.workoutId);
        if (w == null) {
            return;
        }

        // Pull staged values
        String type = stagedType.get() == null ? "" : stagedType.get().trim();
        String notes = stagedNotes.get() == null ? "" : stagedNotes.get().trim();
        LocalDate date = stagedDate.get();
        int hours = stagedHours.get();
        int minutes = stagedMinutes.get();

        // Convert to your HH.MM storage convention (minutes as hundredths)
        double time = hours + (minutes / 100.0);

        if (time <= 0.0) {
            showAlert(INFORMATION, "Duration must be greater than 0.\n", "Please set hours or minutes before saving.");
            return;
        }

        try {
            w.setType(type);     // throws if blank/null
            w.setNotes(notes);   // throws if blank/null
            w.setDate(date);     // throws if null
            w.setTime(time);     // throws if <= 0
        } 
        catch (IllegalArgumentException e) {
            showAlert(ERROR, "Save Error", "Could not add new workout fields: " + e.getMessage());
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
            workoutService.updateWorkout(workoutId, w);
            showAlert(INFORMATION, "Saved", "Workouts saved to file.");
        } 
        catch (IOException e) {
            showAlert(ERROR, "Save Error", "Could not save workouts: " + e.getMessage());
        }
    }

    /**
     * Adds a new CLONED exercise to the editor only (not the model until Save).
     * <p>
     * Creates a new exercise with the default values and adds it to the internal list.
     * </p>
     */
    @FXML
    private void handleAdd() {
        Exercise ex = new Exercise("New exercise", 8, 0.0, null); // id auto-UUID
        exerciseItems.add(ex);

        int row = exerciseItems.size() - 1;
        exerciseTable.getSelectionModel().clearAndSelect(row, nameColumn);
        exerciseTable.scrollTo(row);
        exerciseTable.edit(row, nameColumn);
    }

    /**
     * Deletes the selected exercises from the editor only (not the model until Save).
     * <p>
     * Removes the selected exercises from the internal list and refreshes the table.
     * </p>
     */
    @FXML
    private void handleDelete() {
        var selected = new ArrayList<>(exerciseTable.getSelectionModel().getSelectedItems());
        if (!selected.isEmpty()) {
            exerciseItems.removeAll(selected);
        }
    }

    /**
     * Displays a simple message for the Info-Button.
     * <p>
     * Shows a guide for the workout edit functionality.
     * </p>
     */
    @FXML
    private void handleInfo() {
        String infoText = """
                     Workout Edit Guide
            
            • Edit exercise: Double-click the table-cell
            • Add exercise: Click "Add set"
            • Delete exercise: mark & click "Delete Selected"
            • Reorder exercises: drag and drop rows

            • Click "Save" to confirm changes
            • Click "Back" to discard changes & return home
            """;

        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
            "About Workout Tracker", infoText);
    }

    // ---- edit commit handlers ----
    /**
     * Handles the commit event for the name column.
     * <p>
     * If the new value is not blank, sets the name of the exercise to the new value.
     * Otherwise, refreshes the table.
     * </p>
     * @param evt the cell edit event
     */
    private void onNameEditCommit(TableColumn.CellEditEvent<Exercise, String> evt) {
        String v = (evt.getNewValue() == null) ? "" : evt.getNewValue().trim();
        if (!v.isBlank()) {
            evt.getRowValue().setName(v);
        }
        else {
            exerciseTable.refresh();
        }
    }

    /**
     * Handles the commit event for the repetitions column.
     * <p>
     * If the new value is not null and is greater than or equal to 0, sets the repetitions of the exercise to the new value.
     * Otherwise, refreshes the table.
     * </p>
     * @param evt the cell edit event
     */
    private void onRepsEditCommit(TableColumn.CellEditEvent<Exercise, Integer> evt) {
        Integer v = evt.getNewValue();
        if (v != null && v >= 0) {
            evt.getRowValue().setRepetitions(v);
        }
        else {
            exerciseTable.refresh();
        }
    }

    /**
     * Handles the commit event for the weight column.
     * <p>
     * If the new value is not null and is greater than or equal to 0.0, sets the weight of the exercise to the new value.
     * Otherwise, refreshes the table.
     * </p>
     * @param evt the cell edit event
     */
    private void onWeightEditCommit(TableColumn.CellEditEvent<Exercise, Double> evt) {
        Double v = evt.getNewValue();
        if (v != null && v >= 0.0) {
            evt.getRowValue().setWeight(v);
        }
        else {
            exerciseTable.refresh();
        }
    }


    // ======================== INITIALIZE-FUNCTIONS ========================

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

    /**
     * Initializes the table columns by binding them to {@link Exercise} properties.
     * <p>
     * Sets up the data source, property value factories, and a computed 1-based
     * "Set #" index column that reflects the row position.
     * </p>
     */
    private void initializeColumns() {
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
    }

    /**
     * Configures selection and inline editing behavior for the exercise table.
     * <p>
     * Enables in-place editing for name, repetitions, and weight columns,
     * wires edit-commit handlers, and binds the delete button state to
     * the current selection.
     * </p>
     */
    private void initializeSelectionAndEditing() {
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

    /**
     * Binds the staged header properties to their corresponding UI controls.
     * <p>
     * Ensures two-way synchronization between the type, notes, and date fields
     * and the controller’s staged values.
     * </p>
     */
    private void initializeHeaderBindings() {
        // Bind staged properties to the header fields
        typeField.textProperty().bindBidirectional(stagedType);
        notesArea.textProperty().bindBidirectional(stagedNotes);
        datePicker.valueProperty().bindBidirectional(stagedDate);
    }

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
     * Enables drag-and-drop row reordering on exerciseTable.
     * <p>
     * Allows the user to reorder the exercises in the table by dragging and dropping them.
     * </p>
     */
    private void installRowReorderDnD() {
        final var items = exerciseTable.getItems();

        exerciseTable.setRowFactory(tv -> {
            TableRow<Exercise> row = new TableRow<>();

            row.setOnDragDetected(e -> {
                if (!row.isEmpty()) {
                    int index = row.getIndex();
                    var db = row.startDragAndDrop(TransferMode.MOVE);
                    var cc = new ClipboardContent();
                    cc.putString(Integer.toString(index));
                    db.setContent(cc);
                    e.consume();
                }
            });

            row.setOnDragOver(e -> {
                var db = e.getDragboard();
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    if (row.isEmpty() || row.getIndex() != draggedIndex) {
                        e.acceptTransferModes(TransferMode.MOVE);
                        e.consume();
                    }
                }
            });

            row.setOnDragDropped(e -> {
                var db = e.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    Exercise draggedItem = items.remove(draggedIndex);

                    int dropIndex;
                    if (row.isEmpty()) {
                        // dropped on the "placeholder"/empty row area -> append
                        dropIndex = items.size();
                    } 
                    else {
                        // decide before/after using scene coordinates for reliability
                        double sceneY = e.getSceneY();
                        Bounds rb = row.localToScene(row.getBoundsInLocal());
                        boolean after = sceneY > (rb.getMinY() + rb.getHeight() / 2.0);
                        dropIndex = row.getIndex() + (after ? 1 : 0);
                    }

                    if (draggedIndex < dropIndex) {
                        dropIndex--;  // account for removal shift
                    }
                    dropIndex = Math.max(0, Math.min(dropIndex, items.size()));

                    items.add(dropIndex, draggedItem);
                    exerciseTable.getSelectionModel().clearAndSelect(dropIndex);
                    success = true;
                }
                e.setDropCompleted(success);
                e.consume();
            });

            row.setOnDragEntered(e -> row.setOpacity(0.3));
            row.setOnDragExited(e -> row.setOpacity(1.0));

            return row;
        });

        // ---- Table-level fallback: only when NOT over a row and NOT over header ----
        exerciseTable.setOnDragOver(e -> {
            var db = e.getDragboard();
            if (!db.hasString()) {
                return;
            }
            Node target = (Node) e.getTarget();
            if (getRowAncestor(target) == null && !isInHeader(target)) {
                // We're in the blank area below the last row (or empty table)
                e.acceptTransferModes(TransferMode.MOVE);
                e.consume();
            }
        });

        exerciseTable.setOnDragDropped(e -> {
            var db = e.getDragboard();
            if (!db.hasString()) {
                return;
            }
            Node target = (Node) e.getTarget();
            if (getRowAncestor(target) == null && !isInHeader(target)) {
                int draggedIndex = Integer.parseInt(db.getString());
                if (draggedIndex >= 0 && draggedIndex < items.size()) {
                    Exercise draggedItem = items.remove(draggedIndex);
                    items.add(draggedItem); // append to bottom
                    exerciseTable.getSelectionModel().clearAndSelect(items.size() - 1);
                    e.setDropCompleted(true);
                    e.consume();
                }
            }
        });
    }

    /* ---------- helpers ---------- */

    /**
     * Gets the ancestor row of a given node in the table.
     * @param n the node to find the ancestor row of
     * @return the ancestor row of the given node
     */
    private static TableRow<?> getRowAncestor(Node n) {
        while (n != null && !(n instanceof TableRow)) {
            n = n.getParent();
        }
        return (TableRow<?>) n;
    }

    /**
     * Checks if a given node is in the header of the table.
     * @param n the node to check if it is in the header of the table
     * @return true if the given node is in the header of the table, false otherwise
     */
    private static boolean isInHeader(Node n) {
        while (n != null) {
            var sc = n.getStyleClass();
            if (sc != null && (sc.contains("column-header") || sc.contains("column-header-background"))) {
                return true; // header or its background
            }
            n = n.getParent();
        }
        return false;
    }
}
