package ui;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import core.service.RestWorkoutClient;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;
/**
 * UI integration tests for the Workout Tracker JavaFX interface.
 * <p>
 * This test suite boots the MainView.fxml into a real JavaFX {@link Stage} and
 * interacts with controls using TestFX, asserting behaviors such as adding a
 * workout, creating exercises, and deleting selected exercises.
 * </p>
 */
public class UiWorkoutTest extends ApplicationTest {

    private static final String RUN_TAG = "UI-" + System.nanoTime() + "-"; //unique per testrun
    private RestWorkoutClient restClient;

    /* Start the JavaFX application before tests */
    @Override
    public void start(Stage stage) throws Exception {
        new WorkoutTrackerApp().start(stage);
    }

    /* Setup RestWorkoutClient before each test */
    @BeforeEach
    void setupClient() {
        restClient = new RestWorkoutClient();
    }
    /**
    * Cleans up any workouts created during UI tests by deleting those with notes
    * starting with the unique RUN_TAG.
    */
    @AfterEach
    void cleanupUiData() {
        try {
            for (core.Workout w : restClient.getAllWorkouts()) {
                String notes = w.getNotes();
                if (notes != null && notes.startsWith(RUN_TAG) && w.getId() != null) {
                    try {
                        restClient.deleteWorkout(w.getId());
                    } catch (IOException ioe) {
                        System.err.println("Cleanup delete failed for id " + w.getId() + ": " + ioe.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Cleanup list failed: " + e.getMessage());
        }
    }

    /**
     * Verifies that clicking the "add workout" control opens the form,
     * filling out all fields and adding at least one exercise results in
     * a new workout row present in the table, identified by unique notes.
     **/
@Test
void handleAddWorkout_createsRowWithUniqueNotes() {
    clickOn("#add_workout");

    WaitForAsyncUtils.waitForFxEvents();
    var a = lookup(".dialog-pane").tryQuery();
    if (a.isPresent()) { clickOn("OK"); WaitForAsyncUtils.waitForFxEvents(); }

    String unique = RUN_TAG  + System.currentTimeMillis();

    interact(() -> {
        lookup("#typeField").<TextField>query().setText("Strength");
        lookup("#notesField").<TextArea>query().setText(unique);
        lookup("#datePicker").<DatePicker>query().setValue(java.time.LocalDate.now());
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(1);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(30);
        lookup("#exerciseNameField").<TextField>query().setText("Squat");
        lookup("#comboBoxReps").<ComboBox<Integer>>query().setValue(10);
        lookup("#spinnerWeight").<Spinner<Double>>query().getValueFactory().setValue(100.0);
    });
    clickOn("#addExerciseButton");
    WaitForAsyncUtils.waitForFxEvents();

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    var a2 = lookup(".dialog-pane").tryQuery();
    if (a2.isPresent()) { clickOn("OK"); WaitForAsyncUtils.waitForFxEvents(); }

    for (int i = 0; i < 50; i++) {
        WaitForAsyncUtils.waitForFxEvents();
        var tableById = lookup("#workoutTable").tryQuery();
        var tableByClass = lookup(".table-view").tryQuery();
        if (tableById.isPresent() || tableByClass.isPresent()) break;
        sleep(80);
    }

    javafx.scene.control.TableView<?> table =
        lookup("#workoutTable").tryQuery().isPresent()
        ? lookup("#workoutTable").query()
        : lookup(".table-view").query();

    boolean exists = table.getItems().stream().anyMatch(o -> {
        if (o instanceof core.Workout w) {
            return unique.equals(w.getNotes()) && "Strength".equals(w.getType());
        }
        return String.valueOf(o).contains(unique);
    });
    assertTrue(exists);
}
    /**
     * Verifies that, within the workout form, selecting an exercise row reveals the
     * delete button, clicking the delete button removes exactly one row, and the
     * delete button is hidden again after deletion.
     */
@Test
void handleDeleteExercise_removesSelectedRow_andHidesButton() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    var dlg = lookup(".dialog-pane").tryQuery();
    if (dlg.isPresent()) { clickOn("OK"); WaitForAsyncUtils.waitForFxEvents(); }

    interact(() -> {
        lookup("#exerciseNameField").<TextField>query().setText("A");
        lookup("#comboBoxReps").<ComboBox<Integer>>query().setValue(5);
        lookup("#spinnerWeight").<Spinner<Double>>query().getValueFactory().setValue(10.0);
    });
    clickOn("#addExerciseButton");
    WaitForAsyncUtils.waitForFxEvents();

    interact(() -> {
        lookup("#exerciseNameField").<TextField>query().setText("B");
        lookup("#comboBoxReps").<ComboBox<Integer>>query().setValue(6);
        lookup("#spinnerWeight").<Spinner<Double>>query().getValueFactory().setValue(12.0);
    });
    clickOn("#addExerciseButton");
    WaitForAsyncUtils.waitForFxEvents();

    @SuppressWarnings("unchecked")
    javafx.scene.control.TableView<?> exerciseTable = lookup("#exerciseTable").query();

    int before = exerciseTable.getItems().size();
    assertTrue(before >= 2, "Expecting at least two rows before deletion");

    clickOn(".table-row-cell");
    WaitForAsyncUtils.waitForFxEvents();

    var deleteBtn = lookup("#deleteExerciseButton").query();
    assertTrue(deleteBtn.isVisible(), "Delete-button should be visible after selecting a row");

    clickOn("#deleteExerciseButton");
    WaitForAsyncUtils.waitForFxEvents();

    var maybeAlert = lookup(".dialog-pane").tryQuery();
    if (maybeAlert.isPresent()) { clickOn("OK"); WaitForAsyncUtils.waitForFxEvents(); }

    int after = exerciseTable.getItems().size();
    assertEquals(before - 1, after, "Expecting one less row after deletion");

    assertFalse(deleteBtn.isVisible(), "Delete-button should be hidden after deletion");
}
/**
 * Ensures validation fails when the workout type is blank.
 * <p><b>Setup:</b> Open the add-workout form and fill all fields with valid values
 * except {@code type}, which is whitespace.</p>
 * <p><b>Action:</b> Click "Add Workout".</p>
 * <p><b>Expected:</b> A validation alert is shown complaining that the type must not be empty.</p>
 */
@Test
void validation_fails_when_type_is_empty() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    interact(() -> {
        lookup("#typeField").<TextField>query().setText("   "); // empty
        lookup("#notesField").<TextArea>query().setText("UI-" + System.currentTimeMillis());
        lookup("#datePicker").<DatePicker>query().setValue(java.time.LocalDate.now());
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(0);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(30);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    // Expect an alert from validWorkoutData
    assertTrue(lookup(".dialog-pane").tryQuery().isPresent(), "Expected validation alert for empty type");
    clickOn("OK"); 
}
/**
 * Ensures validation fails when the date is not provided.
 * <p><b>Setup:</b> Open the add-workout form and fill all fields with valid values
 * except {@code date}, which is set to {@code null}.</p>
 * <p><b>Action:</b> Click "Add Workout".</p>
 * <p><b>Expected:</b> A validation alert is shown complaining that the date must not be empty.</p>
 */
@Test
void validation_fails_when_date_is_null() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    interact(() -> {
        lookup("#typeField").<TextField>query().setText("Strength");
        lookup("#notesField").<TextArea>query().setText("UI-" + System.currentTimeMillis());
        lookup("#datePicker").<DatePicker>query().setValue(null); // null date
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(0);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(30);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(lookup(".dialog-pane").tryQuery().isPresent(), "Expected validation alert for null date");
    clickOn("OK");
}
/**
 * Ensures validation fails when the notes field is shorter than the minimum allowed length.
 * <p><b>Setup:</b> Open the add-workout form and fill all fields with valid values
 * except {@code notes}, which contains fewer than {@code UiConstants.MIN_NOTE_LENGTH} characters.</p>
 * <p><b>Action:</b> Click "Add Workout".</p>
 * <p><b>Expected:</b> A validation alert is shown indicating notes are too short.</p>
 */
@Test
void validation_fails_when_notes_too_short() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    interact(() -> {
        lookup("#typeField").<TextField>query().setText("Strength");
        lookup("#notesField").<TextArea>query().setText("ab"); // shorter than MIN_NOTE_LENGTH=3
        lookup("#datePicker").<DatePicker>query().setValue(java.time.LocalDate.now());
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(0);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(30);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(lookup(".dialog-pane").tryQuery().isPresent(), "Expected validation alert for short notes");
    clickOn("OK");
}
/**
 * Ensures validation fails when the notes field exceeds the maximum allowed length.
 * <p><b>Setup:</b> Open the add-workout form and fill all fields with valid values
 * except {@code notes}, which contains {@code UiConstants.MAX_NOTE_LENGTH + 1} characters.</p>
 * <p><b>Action:</b> Click "Add Workout".</p>
 * <p><b>Expected:</b> A validation alert is shown indicating notes are too long.</p>
 */
@Test
void validation_fails_when_notes_too_long() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    final int maxLen = UiConstants.MAX_NOTE_LENGTH; // import UiConstants if needed
    String tooLong = "X".repeat(maxLen + 1);

    interact(() -> {
        lookup("#typeField").<TextField>query().setText("Strength");
        lookup("#notesField").<TextArea>query().setText(tooLong);
        lookup("#datePicker").<DatePicker>query().setValue(java.time.LocalDate.now());
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(0);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(30);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(lookup(".dialog-pane").tryQuery().isPresent(), "Expected validation alert for long notes");
    clickOn("OK");
}
/**
 * Ensures validation fails when workout duration is missing or zero.
 * <p><b>Setup:</b> Open the add-workout form and fill type, notes, and date with valid values.</p>
 * <p><b>Action:</b>
 *   <ol>
 *     <li>Submit with both {@code hours} and {@code minutes} set to {@code null} (if allowed by the UI).</li>
 *     <li>Submit again with {@code hours = 0} and {@code minutes = 0}.</li>
 *   </ol>
 * </p>
 * <p><b>Expected:</b> In both cases, a validation alert is shown instructing the user to select hours and minutes.</p>
 */
@Test
void validation_fails_when_duration_missing_or_zero() {
    clickOn("#add_workout");
    WaitForAsyncUtils.waitForFxEvents();

    // Case 1: both null (if your ComboBoxes allow null)
    interact(() -> {
        lookup("#typeField").<TextField>query().setText("Strength");
        lookup("#notesField").<TextArea>query().setText("UI-" + System.currentTimeMillis());
        lookup("#datePicker").<DatePicker>query().setValue(java.time.LocalDate.now());
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(null);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(null);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();
    if (lookup(".dialog-pane").tryQuery().isPresent()) {
        clickOn("OK");
    }

    // Case 2: sum == 0
    interact(() -> {
        lookup("#comboBoxHours").<ComboBox<Integer>>query().setValue(0);
        lookup("#comboBoxMinutes").<ComboBox<Integer>>query().setValue(0);
    });

    clickOn("Add Workout");
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(lookup(".dialog-pane").tryQuery().isPresent(), "Expected validation alert for zero duration");
    clickOn("OK");
}
}
