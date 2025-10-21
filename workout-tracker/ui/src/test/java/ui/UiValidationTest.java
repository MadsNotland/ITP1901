package ui;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestFX tests for the Workout Tracker UI.
 *
 * Focus:
 * - ComboBox initialization values in WorkoutFormView
 * - Validation alert on empty type
 * - End-to-end flow: Add -> navigate back -> verify row in table
 */
class UiValidationTest extends ApplicationTest {

    private MainController controller;
    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

     /**
     * Verifies that the ComboBoxes in the WorkoutFormView are initialized correctly.
     */
    @Test
    void comboBoxes_areInitializedWithValues() {
        // Navigate to form view
        clickOn("#add_workout");

        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        assertNotNull(hours);
        assertNotNull(minutes);

        assertTrue(hours.getItems().contains(0), "Hours should contain 0");
        assertTrue(hours.getItems().contains(10), "Hours should contain 10");
        assertTrue(minutes.getItems().contains(0), "Minutes should contain 0");
        assertTrue(minutes.getItems().contains(55), "Minutes should contain 55");
    }

    /**
     * Ensures that an alert dialog is shown when the user tries to add a workout without a type.
     */
    @Test
    void validation_showsAlert_onEmptyType() {
        clickOn("#add_workout"); // navigate to form

        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();
        TextArea notesField = lookup("#notesField").query();

        interact(() -> {
            datePicker.setValue(LocalDate.of(2025, 9, 15));
            hours.setValue(1);
            minutes.setValue(30);
            notesField.setText("Nice run");
        });

        clickOn("Add Workout");

        DialogPane dialog = lookup(".dialog-pane").query();
        assertTrue(dialog.getContentText().toLowerCase().contains("type"));
    }

    /**
     * Tests the full user flow:
     * Add a workout through the form, navigate back, and verify that it appears in the table.
     */
    @Test
    void addWorkout_success_addsRowToTable() {
        // Start in table view
        TableView<?> table = lookup("#workoutTable").queryTableView();
        int before = table.getItems().size();

        clickOn("#add_workout"); // Go to form

        TextField typeField = lookup("#typeField").query();
        TextArea notesField = lookup("#notesField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();

        interact(() -> {
            typeField.setText("Run");
            notesField.setText("Morning jog");
            datePicker.setValue(LocalDate.of(2025, 9, 16));
            hours.setValue(1);
            minutes.setValue(30);
        });

        clickOn("Add Workout"); // should navigate back to table
        TableView<?> updatedTable = lookup("#workoutTable").queryTableView();
        int after = updatedTable.getItems().size();

        assertEquals(before + 1, after, "Table should have one more workout after adding");
    }
}
