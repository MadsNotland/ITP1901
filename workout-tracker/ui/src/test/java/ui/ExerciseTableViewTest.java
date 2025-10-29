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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * TestFX UI tests for ExerciseTableView.
 * Loads the view directly, injects a snapshot of exercises, and verifies
 * that rows and the computed "Set #" column render as expected.
 */
class ExerciseTableViewTest extends ApplicationTest {

    private ExerciseTableController controller;

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

        // Populate the table via snapshot (no navigation / services involved)
        controller.setWorkout(sampleWorkout());
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

    /** Minimal, valid Workout with two exercises for driving the UI. */
    private static Workout sampleWorkout() {
        Exercise e1 = new Exercise("Warmup", 0, 0.0, "ex-1");
        Exercise e2 = new Exercise("Bench Press", 5, 80.0, "ex-2");
        return new Workout("Gym", "Chest day", LocalDate.now(), 1.0, List.of(e1, e2));
    }
}
