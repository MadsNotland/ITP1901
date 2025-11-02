package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import core.Exercise;
import core.Workout;
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
 * Loads the view directly, injects a snapshot of exercises, and verifies
 * that rows and the computed "Set #" column render as expected.
 */
class ExerciseTableViewTest extends ApplicationTest {

    private ExerciseTableController controller;
    private Workout sample;

    /**
     * TestFX setup:
     * - Load ExerciseTableView.fxml
     * - Get controller and inject a sample Workout (snapshot into table)
     * - Show the scene
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseTableView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Create sample workout with two exercises
        sample = sampleWorkout();
        // Populate the table via snapshot (no navigation / services involved)
        controller.setWorkout(sample);
        // Ensure FX bindings/cell factories have run before assertions
        WaitForAsyncUtils.waitForFxEvents();

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    /** Table should list both exercises from the injected snapshot, in order. */
    @Test
    void table_populates_withExercises() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        assertEquals(2, table.getItems().size(), "Should list exactly two exercises");
        assertEquals("Warmup", table.getItems().get(0).getName());
        assertEquals("Bench Press", table.getItems().get(1).getName());
    }

    /** First column is the computed 1-based "Set #" index; verify values for first two rows. */
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

    /** Header: initial binding/display from Workout. */
    @Test
    void header_showsWorkoutFields_initialValues() {
        TextField typeField = lookup("#typeField").query();
        DatePicker datePicker = lookup("#datePicker").query();
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").query();
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").query();
        TextArea notesArea = lookup("#notesArea").query();

        assertEquals("Gym", typeField.getText());
        assertEquals(sample.getDate(), datePicker.getValue());
        assertEquals(Integer.valueOf(1), comboHours.getValue(), "Hours should reflect 1.00");
        assertEquals(Integer.valueOf(0), comboMinutes.getValue(), "Minutes should reflect 00");
        assertEquals("Chest day", notesArea.getText());
    }

    /** Edit name via double-click + enter -> model updates. */
    @Test
    void editing_name_commitsOnEnter_updatesModel() {
        TextField typeField = lookup("#typeField").query();

        doubleClickOn("#typeField");
        eraseText(typeField.getText().length());
        write("Leg Day");
        // ENTER triggers onAction in controller
        type(javafx.scene.input.KeyCode.ENTER);

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Leg Day", sample.getType());
    }

    /** Edit notes via double-click + blur -> model updates. */
    @Test
    void editing_notes_commitsOnBlur_updatesModel() {
        TextArea notesArea = lookup("#notesArea").query();

        doubleClickOn("#notesArea");
        eraseText(notesArea.getText().length());
        write("New notes from UI test");
        // move focus to table to trigger blur
        clickOn("#exerciseTable");

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("New notes from UI test", sample.getNotes());
    }

    /** Edit date via double-click + setValue + blur -> model updates. */
    @Test
    void editing_date_commitsOnBlur_updatesModel() {
       DatePicker datePicker = lookup("#datePicker").query();
        LocalDate newDate = LocalDate.now().plusDays(3);

        // Put the picker into "edit mode" like the controller would after a double click
        interact(() -> {
            datePicker.setEditable(true);
            datePicker.setValue(newDate);
            datePicker.hide();
            // Fire the same event path the controller listens to
            datePicker.fireEvent(new javafx.event.ActionEvent());
        });

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(newDate, sample.getDate());
    }

    /** Changing time via ComboBox-values → model updates instantly. */
    @Test
    void editing_duration_commitsImmediately_updatesModel() {
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").query();
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").query();

        // Start from 1:00 (from sample)
        assertEquals(1.00, sample.getTime(), 0.0001);

        // Change minutes to 30 → expect 1.30
        interact(() -> comboMinutes.setValue(30));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1.30, sample.getTime(), 0.0001);

        // Change hours to 2 → expect 2.30
        interact(() -> comboHours.setValue(2));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2.30, sample.getTime(), 0.0001);
    }

    /** Minimal, valid Workout with two exercises for driving the UI. */
    private static Workout sampleWorkout() {
        Exercise e1 = new Exercise("Warmup", 0, 0.0, "ex-1");
        Exercise e2 = new Exercise("Bench Press", 5, 80.0, "ex-2");
        return new Workout("Gym", "Chest day", LocalDate.now(), 1.0, List.of(e1, e2));
    }
}
