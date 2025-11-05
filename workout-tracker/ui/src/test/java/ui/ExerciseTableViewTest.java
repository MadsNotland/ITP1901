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

   /** Header shows values seeded from Workout on load. */
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

    /** Edit name via double-click + Enter -> UI updates, model remains unchanged. */
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

     /** Edit notes via double-click + blur -> UI updates, model remains unchanged. */
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

    /** Edit date via double-click + setValue + action -> UI updates, model remains unchanged. */
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

    /** Changing time via ComboBoxes -> UI staged values change, model time remains unchanged. */
    @Test
    void editing_duration_updatesUI_only_notModel() {
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboHours = lookup("#comboBoxHours").query();
        @SuppressWarnings("unchecked")
        ComboBox<Integer> comboMinutes = lookup("#comboBoxMinutes").query();

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

    /** Clicking "Add" adds a row to the snapshot table (no need to call private handleAdd). */
    @Test
    void addButton_click_addsRow() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        int before = table.getItems().size();

        // simulate user click
        clickOn("#addButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(before + 1, table.getItems().size(), "Add button should append a row");
    }

    /** Select a row and click "Delete" -> row removed from snapshot. */
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

    /** After add/delete, the computed "Set #" column reindexes to 1..n. */
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




    /** Minimal, valid Workout with two exercises for driving the UI. */
    private static Workout sampleWorkout() {
        Exercise e1 = new Exercise("Warmup", 0, 0.0, "ex-1");
        Exercise e2 = new Exercise("Bench Press", 5, 80.0, "ex-2");
        return new Workout("Gym", "Chest day", LocalDate.now(), 1.0, List.of(e1, e2));
    }
}
