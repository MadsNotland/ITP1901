package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import core.Exercise;
import core.Workout;
import core.WorkoutTracker;
import core.service.WorkoutService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * TestFX UI tests for ExerciseTableView.
 * <p>Verifies that:
 * <ul>
 *   <li>Exercise rows and the computed "Set #" render correctly from a snapshot.</li>
 *   <li>Header fields (type/date/duration/notes) show values seeded from the Workout.</li>
 *   <li>Edits to header fields update only the staged UI values and DO NOT mutate the underlying Workout.</li>
 * </ul>
 * </p>
 */
class ExerciseTableViewTest extends ApplicationTest {

    private ExerciseTableController controller;
    private Workout sample;
    private TestWorkoutService workoutService;

    
    /**
     * TestFX setup:
     * - Load ExerciseTableView.fxml
     * - Get controller and inject a sample Workout (snapshot into table)
     * - Show the scene
     * 
     * @param stage the stage to show the scene on
     * @throws Exception if the scene cannot be loaded or shown
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseTableView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        sample = sampleWorkout();
        workoutService = new TestWorkoutService(sample);

        controller.setWorkoutService(workoutService);
        controller.setWorkout(sample);

        WaitForAsyncUtils.waitForFxEvents();

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    /**
     * Reset the in-memory test data to a clean default state before each test run
     * and flush any pending JavaFX events to ensure the UI is stable.
     */
    @BeforeEach
    void resetFlags() {
        if (workoutService != null) {
            workoutService.reset();
        }
    }

    /** Table should list both exercises from the injected snapshot, in order.
     * <p>
     * Verifies that the table lists both exercises from the injected snapshot, in order.
     * </p>
     */
    @Test
    void table_populates_withExercises() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        assertEquals(2, table.getItems().size(), "Should list exactly two exercises");
        assertEquals("Warmup", table.getItems().get(0).getName());
        assertEquals("Bench Press", table.getItems().get(1).getName());
    }

    /** First column is the computed 1-based "Set #" index; verify values for first two rows.
     * <p>
     * Verifies that the first column is the computed 1-based "Set #" index and that the values for the first two rows are correct.
     * </p>
     */
    @Test
    void setColumn_showsSequentialIndices() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();

        // Column 0 is "Set #"; cast so we can access cell values by row index
        @SuppressWarnings("unchecked")
        TableColumn<Exercise, Number> setCol =
        (TableColumn<Exercise, Number>) table.getColumns().get(0);

        int first  = setCol.getCellObservableValue(0).getValue().intValue();
        int second = setCol.getCellObservableValue(1).getValue().intValue();

        assertEquals(1, first,  "First row should display set #1");
        assertEquals(2, second, "Second row should display set #2");
    }

    /** Header shows values seeded from Workout on load.
     * <p>
     * Verifies that the header shows the values seeded from the Workout on load.
     * </p>
     */
    @Test
    void header_showsWorkoutFields_initialValues() {
        TextField typeField = lookup("#typeField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").<ComboBox<Integer>>query();
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").<ComboBox<Integer>>query();
        TextArea notesArea = lookup("#notesArea").query();

        assertEquals("Gym", typeField.getText());
        assertEquals(sample.getDate(), datePicker.getValue());
        assertEquals(Integer.valueOf(1), comboHours.getValue(), "Hours should reflect 1.00");
        assertEquals(Integer.valueOf(0), comboMinutes.getValue(), "Minutes should reflect 00");
        assertEquals("Chest day", notesArea.getText());
    }

    /** Edit name via double-click + Enter -> UI updates, model remains unchanged.
     * <p>
     * Verifies that editing the name via double-click + Enter updates the UI and the model remains unchanged.
     * </p>
     */
    @Test
    void editing_name_updatesUI_only_notModel() {
        TextField typeField = lookup("#typeField").query();

        doubleClickOn("#typeField");
        eraseText(typeField.getText().length());
        write("Leg Day");
        // ENTER triggers onAction (controller just exits edit mode)
        type(javafx.scene.input.KeyCode.ENTER);

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Leg Day", typeField.getText(), "UI should show staged value");
        assertEquals("Gym", sample.getType(), "Model should remain unchanged");
    }

     /** Edit notes via double-click + blur -> UI updates, model remains unchanged.
      * <p>
      * Verifies that editing the notes via double-click + blur updates the UI and the model remains unchanged.
      * </p>
      */
    @Test
    void editing_notes_updatesUI_only_notModel() {
        TextArea notesArea = lookup("#notesArea").query();

        doubleClickOn("#notesArea");
        eraseText(notesArea.getText().length());
        write("New notes from UI test");
        // move focus to table to trigger blur
        clickOn("#exerciseTable");

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("New notes from UI test", notesArea.getText(), "UI should show staged value");
        assertEquals("Chest day", sample.getNotes(), "Model should remain unchanged");
    }

    /** Edit date via double-click + setValue + action -> UI updates, model remains unchanged.
     * <p>
     * Verifies that editing the date via double-click + setValue + action updates the UI and the model remains unchanged.
     * </p>
     */
    @Test
    void editing_date_updatesUI_only_notModel() {
        DatePicker datePicker = lookup("#datePicker").query();
        LocalDate newDate = LocalDate.now().plusDays(3);

        // Simulate edit mode + value change
        interact(() -> {
            datePicker.setEditable(true);
            datePicker.setValue(newDate); // bound to staged date
            datePicker.hide();
            datePicker.fireEvent(new javafx.event.ActionEvent()); // controller exits edit mode
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(newDate, datePicker.getValue(), "UI should show staged date");
        assertNotEquals(newDate, sample.getDate(), "Model should remain unchanged");
    }

    /** Changing time via ComboBoxes -> UI staged values change, model time remains unchanged.
     * <p>
     * Verifies that changing the time via ComboBoxes updates the UI staged values and the model time remains unchanged.
     * </p>
     */
    @Test
    void editing_duration_updatesUI_only_notModel() {
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").<ComboBox<Integer>>query();
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").<ComboBox<Integer>>query();

        // Start from 1:00 (from sample)
        assertEquals(1.00, sample.getTime(), 0.0001);

        // Change minutes to 30 (staged only)
        interact(() -> comboMinutes.setValue(30));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(Integer.valueOf(30), comboMinutes.getValue(), "UI minutes should update");
        assertEquals(1.00, sample.getTime(), 0.0001, "Model time should remain unchanged");

        // Change hours to 2 (staged only)
        interact(() -> comboHours.setValue(2));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(Integer.valueOf(2), comboHours.getValue(), "UI hours should update");
        assertEquals(1.00, sample.getTime(), 0.0001, "Model time should remain unchanged");
    }

    /** Clicking "Add" adds a row to the snapshot table (no need to call private handleAdd).
     * <p>
     * Verifies that clicking the "Add" button adds a row to the snapshot table (no need to call private handleAdd).
     * </p>
     */
    @Test
    void addButton_click_addsRow() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        int before = table.getItems().size();

        // simulate user click
        clickOn("#addButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(before + 1, table.getItems().size(), "Add button should append a row");
    }

    /** Select a row and click "Delete" -> row removed from snapshot.
     * <p>
     * Verifies that selecting a row and clicking the "Delete" button removes the row from the snapshot.
     * </p>
     */
    @Test
    void deleteButton_click_removesSelectedRows() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();

        // ensure there is at least one row to delete; add via UI if needed
        if (table.getItems().isEmpty()) {
            clickOn("#addButton");
            WaitForAsyncUtils.waitForFxEvents();
        }

        int before = table.getItems().size();

        // select last row, then click delete
        interact(() -> table.getSelectionModel().selectLast());
        clickOn("#deleteButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(before - 1, table.getItems().size(), "Delete button should remove selected row(s)");
    }

    /** After add/delete, the computed "Set #" column reindexes to 1..n.
     * <p>
     * Verifies that after adding and deleting a row, the computed "Set #" column reindexes to 1..n.
     * </p>
     * 
     * @throws AssertionError if the set column does not reindex correctly
     */
    @Test
    void setColumn_reindexes_afterAddDelete() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();

        clickOn("#addButton");
        WaitForAsyncUtils.waitForFxEvents();

        @SuppressWarnings("unchecked")
        TableColumn<Exercise, Number> setCol =
            (TableColumn<Exercise, Number>) table.getColumns().get(0);

        for (int i = 0; i < table.getItems().size(); i++) {
            int setNo = setCol.getCellObservableValue(i).getValue().intValue();
            assertEquals(i + 1, setNo, "Row "+i+" should display set #"+(i+1));
        }

        interact(() -> table.getSelectionModel().select(0));
        clickOn("#deleteButton");
        WaitForAsyncUtils.waitForFxEvents();

        for (int i = 0; i < table.getItems().size(); i++) {
            int setNo = setCol.getCellObservableValue(i).getValue().intValue();
            assertEquals(i + 1, setNo, "Row "+i+" should display set #"+(i+1));
        }
    }

    /** Minimal, valid Workout with two exercises for driving the UI. 
     * <p>
     * Creates a sample workout with two exercises.
     * </p>
     * @return the sample workout
    */
    private static Workout sampleWorkout() {
        Exercise e1 = new Exercise("Warmup", 0, 0.0, "ex-1");
        Exercise e2 = new Exercise("Bench Press", 5, 80.0, "ex-2");
        Workout workout = new Workout("Gym", "Chest day", LocalDate.now(), 1.0, List.of(e1, e2));
        // Set an ID for the workout to ensure findWorkout works correctly
        workout.setId(1L);
        return workout;
    }

    /**
     * Test that saving commits staged header changes to the model.
     */
    @Test
    void handleSave_commitsHeaderChangesToModel() {
        // First verify initial state
        assertEquals("Gym", sample.getType());
        
        // Edit header fields using direct approach
        interact(() -> {
            TextField typeField = lookup("#typeField").query();
            typeField.setText("Leg Day");
        });
        
        interact(() -> {
            TextArea notesArea = lookup("#notesArea").query();
            notesArea.setText("Updated notes");
        });
        
        LocalDate newDate = LocalDate.now().plusDays(5);
        interact(() -> {
            DatePicker datePicker = lookup("#datePicker").query();
            datePicker.setValue(newDate);
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify staged values changed but model unchanged
        TextField typeField = lookup("#typeField").query();
        assertEquals("Leg Day", typeField.getText());
        assertEquals("Gym", sample.getType()); // Model unchanged before save
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify model was updated
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals("Leg Day", sample.getType());
        assertEquals("Updated notes", sample.getNotes());
        assertEquals(newDate, sample.getDate());
    }

    /**
     * Test that saving commits exercise changes to the model.
     */
    @Test
    void handleSave_commitsExerciseChangesToModel() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        
        // Edit an exercise using direct approach
        interact(() -> {
            if (!table.getItems().isEmpty()) {
                Exercise firstExercise = table.getItems().get(0);
                firstExercise.setName("Updated Warmup");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify staged value changed but model unchanged
        assertEquals("Updated Warmup", table.getItems().get(0).getName());
        assertEquals("Warmup", sample.getExercises().get(0).getName()); // Model unchanged before save
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify save was attempted and model was updated
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals("Updated Warmup", sample.getExercises().get(0).getName());
    }

    /**
     * Test that saving commits added exercises to the model.
     */
    @Test
    void handleSave_commitsAddedExercisesToModel() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        int initialExerciseCount = sample.getExercises().size();
        
        // Add a new exercise
        clickOn("#addButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify UI updated but model unchanged
        assertEquals(initialExerciseCount + 1, table.getItems().size());
        assertEquals(initialExerciseCount, sample.getExercises().size()); 
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify save was attempted and model was updated
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals(initialExerciseCount + 1, sample.getExercises().size());
    }

    /**
     * Test that saving commits deleted exercises to the model.
     */
    @Test
    void handleSave_commitsDeletedExercisesToModel() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        int initialExerciseCount = sample.getExercises().size();
        
        // Select and delete an exercise
        interact(() -> table.getSelectionModel().select(0));
        clickOn("#deleteButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify UI updated but model unchanged
        assertEquals(initialExerciseCount - 1, table.getItems().size());
        assertEquals(initialExerciseCount, sample.getExercises().size()); // Model unchanged before save
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify save was attempted and model was updated
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals(initialExerciseCount - 1, sample.getExercises().size());
    }

    /**
     * Test that saving with zero duration shows validation error.
     */
    @Test
    void handleSave_showsError_whenDurationIsZero() {
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").<ComboBox<Integer>>query();
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").<ComboBox<Integer>>query();
        
        // Set duration to zero
        interact(() -> {
            comboHours.setValue(0);
            comboMinutes.setValue(0);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Try to save
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // For validation errors, we expect workout not to be updated
        assertFalse(workoutService.wasUpdated(), "Workout should not be updated when validation fails");
        assertEquals(1.00, sample.getTime(), 0.0001, "Model should not be updated when validation fails");
    }

    /**
     * Test that saving preserves exercise order after reordering.
     */
    @Test
    void handleSave_preservesReorderedExercises() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        
        // Get initial order
        String firstExerciseBefore = table.getItems().get(0).getName();
        String secondExerciseBefore = table.getItems().get(1).getName();
        
        // Simulate reordering by swapping the exercises in the table
        interact(() -> {
            List<Exercise> items = new ArrayList<>(table.getItems());
            Exercise first = items.remove(0);
            items.add(first); // Move first to last
            table.getItems().setAll(items);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify UI order changed but model unchanged
        assertEquals(secondExerciseBefore, table.getItems().get(0).getName());
        assertEquals(firstExerciseBefore, table.getItems().get(1).getName());
        assertEquals(firstExerciseBefore, sample.getExercises().get(0).getName()); // Model unchanged
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify save was attempted and model order was updated
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals(secondExerciseBefore, sample.getExercises().get(0).getName());
        assertEquals(firstExerciseBefore, sample.getExercises().get(1).getName());
    }

    /**
     * Test that saving updates the workout time correctly from hours and minutes.
     */
    @Test
    void handleSave_updatesWorkoutTimeCorrectly() {
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").<ComboBox<Integer>>query();
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").<ComboBox<Integer>>query();
        
        // Change time to 2 hours and 30 minutes
        interact(() -> {
            comboHours.setValue(2);
            comboMinutes.setValue(30);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Save changes
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify save was attempted and model time was updated correctly (2.30 hours)
        assertTrue(workoutService.wasUpdated(), "Workout should have been updated");
        assertEquals(2.30, sample.getTime(), 0.0001);
    }

    /**
     * Test that empty type field shows validation error on save.
     */
    @Test
    void handleSave_showsError_whenTypeIsEmpty() {
        // First ensure duration is valid to isolate type validation
        interact(() -> {
            ComboBox<Integer> comboHours = lookup("#comboBoxHours").<ComboBox<Integer>>query();
            comboHours.setValue(1);
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Clear type field using direct approach
        interact(() -> {
            TextField typeField = lookup("#typeField").query();
            typeField.setText("");
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Try to save
        clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        // For validation errors, the model should not be updated
        assertFalse(workoutService.wasUpdated(), "Workout should not be updated when validation fails");
        assertEquals("Gym", sample.getType(), "Model should not be updated when validation fails");
    }

    /**
     * Test implementation of {@link WorkoutService} for UI testing.
     * 
     * <p>This fake service provides a simplified in-memory implementation that:
     * <ul>
     *   <li>Tracks when {@link #updateWorkout(Long, Workout)} is called via {@link #wasUpdated()}</li>
     *   <li>Maintains a single sample workout for all find operations</li>
     *   <li>Updates the sample workout in-place when save operations are performed</li>
     *   <li>Does not perform actual HTTP calls or file I/O operations</li>
     * </ul>
     * </p>
     * 
     * <p>Primary use case: Enables testing of {@link ExerciseTableController#handleSave()} 
     * functionality without requiring a live backend or complex setup.</p>
     */
     private static class TestWorkoutService extends WorkoutService {
        private final Workout sampleWorkout;
        private boolean updated = false;

        TestWorkoutService(Workout sampleWorkout) {
            super(new TestWorkoutClient());
            this.sampleWorkout = sampleWorkout;
        }

        @Override
        public Workout findWorkout(Long id) {
            return sampleWorkout;
        }

        @Override
        public void updateWorkout(Long id, Workout workout) {
            updated = true;
            // Update our sample workout in-place for testing
            sampleWorkout.setType(workout.getType());
            sampleWorkout.setNotes(workout.getNotes());
            sampleWorkout.setDate(workout.getDate());
            sampleWorkout.setTime(workout.getTime());
            sampleWorkout.setExercises(new ArrayList<>(workout.getExercises()));
        }

        public boolean wasUpdated() {
            return updated;
        }

        public void reset() {
            updated = false;
        }

        @Override
        public WorkoutTracker getWorkoutTracker() {
            WorkoutTracker tracker = new WorkoutTracker();
            tracker.addWorkout(sampleWorkout);
            return tracker;
        }
    }

    /**
     * Test implementation of WorkoutClient that doesn't make actual HTTP calls. (fake)
     * <p>
     * This client does nothing and is used to satisfy the super calls in the TestWorkoutService.
     * </p>
     */
    private static class TestWorkoutClient implements core.service.WorkoutClient {
        @Override
        public List<Workout> getAllWorkouts() {
            return new ArrayList<>();
        }

        @Override
        public Workout createWorkout(Workout workout) {
            return workout;
        }

        @Override
        public Workout updateWorkout(Long id, Workout workout) {
            return workout;
        }

        @Override
        public void deleteWorkout(Long id) {
            // No-op for testing
        }
    }

    /**
     * Commits edits for the exercise name column.
     *
     * <p>Trims the new value and applies it to the row's {@link Exercise}
     * only if non-blank; otherwise ignores the change and keeps the
     * previous value. This updates the table's staged/snapshot data only;
     * the underlying {@link Workout} is persisted on Save.</p>
     */
    @Test
    void nameEditCommit_updates_whenNonBlank_and_ignores_blank() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        @SuppressWarnings("unchecked")
        TableColumn<Exercise, String> nameCol =
        (TableColumn<Exercise, String>) table.getColumns().get(1);

        interact(() -> { table.layout(); table.scrollTo(0); });

        WaitForAsyncUtils.waitForFxEvents();

        interact(() -> {
            table.getSelectionModel().clearAndSelect(0, nameCol);
            table.edit(0, nameCol);
        });

        WaitForAsyncUtils.waitForFxEvents();

        TextField editor = from(table).lookup(".text-field").query();
        clickOn(editor);
        eraseText(editor.getText() == null ? 0 : editor.getText().length());
        write("New Warmup");
        type(javafx.scene.input.KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("New Warmup", table.getItems().get(0).getName());

        interact(() -> {
            table.getSelectionModel().clearAndSelect(0, nameCol);
            table.edit(0, nameCol);
        });
        WaitForAsyncUtils.waitForFxEvents();
        editor = from(table).lookup(".text-field").query();
        clickOn(editor);
        eraseText(editor.getText() == null ? 0 : editor.getText().length());
        write("   ");
        type(javafx.scene.input.KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("New Warmup", table.getItems().get(0).getName(),
            "Blank commit should be ignored");
    }

    /** installRowReorderDnD: drag row 2 to upper half of row 1 (before it).
     * <p>
     * Verifies that dragging a row to the upper half of another row reorders the rows correctly.
     * </p>
     */
    @Test
    void dragAndDrop_reorders_rows() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        assertEquals("Warmup", table.getItems().get(0).getName());
        assertEquals("Bench Press", table.getItems().get(1).getName());

        interact(() -> { table.scrollTo(0); table.scrollTo(1); });
        WaitForAsyncUtils.waitForFxEvents();

        javafx.scene.Node row0 = lookup(".table-row-cell").nth(0).query();
        javafx.scene.Node row1 = lookup(".table-row-cell").nth(1).query();
        drag(row1, javafx.scene.input.MouseButton.PRIMARY)
        .moveTo(row0)
        .moveBy(0, -10) 
        .drop();
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Bench Press", table.getItems().get(0).getName(),
            "Second row should be first after DnD");
        assertEquals("Warmup", table.getItems().get(1).getName());
    }
}
