package ui;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import core.Workout;
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
 * Additional UI tests to increase coverage of validation branches and table behavior.
 *
 * Covers:
 * - Validation alerts for null date, short notes, and zero time
 * - Insertion order: newest workout appears at top of the table
 * - Table shows the values we entered
 */
class UiMoreValidationTest extends ApplicationTest {

    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("App.fxml"));
        root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void validation_showsAlert_onNullDate() {
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

    @Test
    void validation_showsAlert_onShortNotes() {
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

    @Test
    void validation_showsAlert_onZeroTime() {
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

    @Test
    void addWorkout_newestAppearsOnTop_andValuesShown() {
        TextField typeField = lookup("#typeField").query();
        TextArea notesField = lookup("#notesField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        ComboBox<Integer> hours = lookup("#comboBoxHours").query();
        ComboBox<Integer> minutes = lookup("#comboBoxMinutes").query();
        TableView<?> table = lookup("#workoutTable").queryTableView();

        // Add first workout
        interact(() -> {
            typeField.setText("Swim");
            notesField.setText("Pool");
            datePicker.setValue(LocalDate.of(2025, 9, 10));
            hours.setValue(1);
            minutes.setValue(0);
        });
        clickOn("Add Workout");

        // Add second workout (should appear at top)
        interact(() -> {
            typeField.setText("Bike");
            notesField.setText("Ride");
            datePicker.setValue(LocalDate.of(2025, 9, 11));
            hours.setValue(0);
            minutes.setValue(30);
        });
        clickOn("Add Workout");

        assertTrue(table.getItems().size() >= 2);
        Object topItem = table.getItems().get(0);
        Workout top = (Workout) topItem;
        assertEquals("Bike", top.getType());
        assertEquals("Ride", top.getNotes());
        assertEquals(LocalDate.of(2025, 9, 11), top.getDate());
        assertEquals(0.30, top.getTime());
    }
}


