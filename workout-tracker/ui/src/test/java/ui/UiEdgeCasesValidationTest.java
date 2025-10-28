package ui;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import core.Workout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Extended TestFX UI tests for the redesigned Workout Tracker application.
 * <p>
 * Focus:
 * <ul>
 *   <li>Validation alerts for null date, short notes, and zero time</li>
 *   <li>End-to-end flow: navigating between table and form</li>
 *   <li>Verifies newest workout appears on top and data is shown correctly</li>
 * </ul>
 * </p>
 */
class UiEdgeCasesValidationTest extends ApplicationTest {

    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Ensures that an alert is shown when date is missing.
     */
    @Test
    void validation_showsAlert_onNullDate() {
        clickOn("#add_workout"); // Navigate to form first
        WaitForAsyncUtils.waitForFxEvents();

        TextField typeField = lookup("#typeField").query();
        TextArea notesField = lookup("#notesField").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        interact(() -> {
            typeField.setText("Run");
            notesField.setText("Good");
            hours.setValue(1);
            minutes.setValue(15);
        });

        clickOn("Add Workout");

        DialogPane dialog = lookup(".dialog-pane").query();
        assertTrue(dialog.getContentText().toLowerCase().contains("date"));
    }

    /**
     * Ensures that an alert is shown when notes are too short.
     */
    @Test
    void validation_showsAlert_onShortNotes() {
        clickOn("#add_workout"); // Navigate to FormView
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.waitForFxEvents(); // Additional wait for form to be ready

        TextField typeField = lookup("#typeField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        interact(() -> {
            typeField.setText("Run");
            datePicker.setValue(LocalDate.of(2025, 9, 15));
            hours.setValue(1);
            minutes.setValue(15);
        });

        clickOn("Add Workout");

        DialogPane dialog = lookup(".dialog-pane").query();
        assertTrue(dialog.getContentText().toLowerCase().contains("notes"));
    }

    /**
     * Ensures that an alert is shown when both hour and minute selections total 0.
     */
    @Test
    void validation_showsAlert_onZeroTime() {
        clickOn("#add_workout"); // Navigate to formview
        WaitForAsyncUtils.waitForFxEvents();
        WaitForAsyncUtils.waitForFxEvents(); // Additional wait for form to be ready


        TextField typeField = lookup("#typeField").query();
        TextArea notesField = lookup("#notesField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        interact(() -> {
            typeField.setText("Run");
            notesField.setText("Okay");
            datePicker.setValue(LocalDate.of(2025, 9, 15));
            hours.setValue(0);
            minutes.setValue(0);
        });

        clickOn("Add Workout");

        DialogPane dialog = lookup(".dialog-pane").query();
        assertTrue(dialog.getContentText().toLowerCase().contains("hours"));
    }

    /**
     * Tests that when adding multiple workouts, the newest appears on top of the table and displays correct values.
     */
    @Test
    void newestWorkoutAppearsOnTopWithCorrectValues() {
        // Step 1: Ensure we're in table view
        WaitForAsyncUtils.waitForFxEvents();
        TableView<Workout> table = lookup("#workoutTable").queryTableView();
        int initialSize = table.getItems().size();

        // Step 2: Navigate to form view
        clickOn("#add_workout");
        WaitForAsyncUtils.waitForFxEvents();
        
        // Wait for form to be loaded and verify elements exist
        WaitForAsyncUtils.waitForFxEvents();
        sleep(100); // Give extra time for form to load
        
        // Step 3: Fill in workout data
        TextField typeField = lookup("#typeField").query();
        TextArea notesField = lookup("#notesField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        LocalDate workoutDate = LocalDate.of(2025, 10, 19);

        interact(() -> {
            typeField.setText("Deadlift");
            notesField.setText("Heavy session");
            datePicker.setValue(workoutDate);
            hours.setValue(1);
            minutes.setValue(0);
        });

        // Step 4: Submit form
        clickOn("Add Workout");
        WaitForAsyncUtils.waitForFxEvents(); // back to table view
        WaitForAsyncUtils.waitForFxEvents(); // additional wait for table to be ready

        // Step 5: Verify table updated
        TableView<Workout> updatedTable = lookup("#workoutTable").queryTableView();
        assertEquals(initialSize + 1, updatedTable.getItems().size(), "Table should contain one more workout");

        // Step 6: Verify newest workout is on top and matches input
        Workout topWorkout = updatedTable.getItems().get(0);
        assertAll("Newest workout should match entered data",
            () -> assertEquals("Deadlift", topWorkout.getType(), "Type should match"),
            () -> assertEquals("Heavy session", topWorkout.getNotes(), "Notes should match"),
            () -> assertEquals(workoutDate, topWorkout.getDate(), "Date should match"),
            () -> assertEquals(1.0, topWorkout.getTime(), 0.001, "Time should match"),
            () -> assertTrue(updatedTable.getItems().get(0).getDate().isAfter(
                    updatedTable.getItems().get(updatedTable.getItems().size() - 1).getDate()),
                    "Newest workout should be on top")
        );
    }
}
