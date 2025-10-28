package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
 * TestFX UI tests for the Exercise table view.
 *
 * This test loads ExerciseTableView.fxml directly (no app navigation),
 * injects a sample Workout into the controller, and verifies:
 *  - The table shows the exercises from the Workout
 *  - The "Set #" column renders sequential 1-based indices
 *
 * Tools used match the rest of the project tests: JUnit 5 + TestFX only.
 */
class ExerciseTableViewTest extends ApplicationTest {

    private ExerciseTableController controller;

    /**
     * TestFX lifecycle hook.
     * Loads the ExerciseTableView FXML, grabs the controller,
     * injects a sample Workout, and shows the scene.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseTableView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Inject data directly—no navigation from MainView needed
        controller.setWorkout(sampleWorkout());
        controller.initializeData();

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    /**
     * Ensure the table is refreshed and all FX events are processed
     * before each test runs (keeps assertions deterministic).
     */
    @BeforeEach
    void sync() {
        controller.refreshTable();
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Verifies that the Exercise table is populated with the two
     * exercises from the injected Workout and preserves their names.
     */
    @Test
    void table_populates_withExercises() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();
        assertEquals(2, table.getItems().size(), "Should list exactly two exercises");
        assertEquals("Warmup", table.getItems().get(0).getName());
        assertEquals("Bench Press", table.getItems().get(1).getName());
    }

    /**
     * Verifies the computed "Set #" column shows sequential indices
     * (1-based) matching the row order.
     */
    @Test
    void setColumn_showsSequentialIndices() {
        TableView<Exercise> table = lookup("#exerciseTable").queryTableView();

        // First column is "Set #"; cast to the correct type for value access
        @SuppressWarnings("unchecked")
        TableColumn<Exercise, Number> setCol =
                (TableColumn<Exercise, Number>) table.getColumns().get(0);

        int first  = setCol.getCellObservableValue(0).getValue().intValue();
        int second = setCol.getCellObservableValue(1).getValue().intValue();

        assertEquals(1, first,  "First row should display set #1");
        assertEquals(2, second, "Second row should display set #2");
    }

    /**
     * Helper: build a minimal, valid Workout with two exercises
     * to drive the UI without touching persistence.
     */
    private static Workout sampleWorkout() {
        Exercise e1 = new Exercise("Warmup", 0, 0.0, "ex-1");
        Exercise e2 = new Exercise("Bench Press", 5, 80.0, "ex-2");
        return new Workout("Gym", "Chest day", LocalDate.now(), 1.0, List.of(e1, e2));
    }
}
