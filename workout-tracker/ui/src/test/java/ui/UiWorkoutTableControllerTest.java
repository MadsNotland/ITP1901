package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class UiWorkoutTableControllerTest extends ApplicationTest {

    private WorkoutTableController controller;
    private FakeWorkoutService workoutService;
    private FakeMainController mainController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutTableView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        workoutService = new FakeWorkoutService();
        mainController   = new FakeMainController();

        controller.setWorkoutService(workoutService);
        controller.setMainController(mainController);
        controller.initializeData();

        stage.setScene(new Scene(root, 900, 600));
        stage.show();

        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Reset the in-memory test data to a clean default state before each test run
     * and flush any pending JavaFX events to ensure the UI is stable.
     */
    @BeforeEach
    void beforeEach() {
        workoutService.resetWithDefaults();
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Verifies that the workout table is populated from the injected
     * {@code WorkoutService} and contains exactly the expected workouts,
     * without relying on row order.
     */
    @Test
    void table_populates_with_workouts() {
        TableView<Workout> table = lookup("#workoutTable").queryTableView();

        assertEquals(2, table.getItems().size(), "Should show two workouts");

        Set<String> types = table.getItems().stream().map(Workout::getType).collect(Collectors.toSet());
        assertEquals(Set.of("Gym A", "Gym B"), types);
    }

    /**
     * Simulates a user double-click on a table row and asserts that the controller
     * navigates to the exercise view with the same {@code Workout} as the selected row.
     * This exercises the private {@code handleRowClick} via actual UI events.
     */
    @Test
    void double_click_on_row_navigates_to_exercise_table() {
        TableView<Workout> table = lookup("#workoutTable").queryTableView();

        clickOn(table);
        interact(() -> table.getSelectionModel().select(0));
        doubleClickOn(table);

        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(mainController.exerciseTableShown, "Should have navigated to the exercise table");
        assertNotNull(mainController.lastWorkoutShown);
        // Confirm that the workout opened is the selected one
        Workout selected = table.getSelectionModel().getSelectedItem();
        assertEquals(selected.getType(), mainController.lastWorkoutShown.getType());
        assertEquals(selected.getDate(), mainController.lastWorkoutShown.getDate());
    }

    // ------------ Fakes for test ------------

    /** Fake MainController that only records navigation. */
    private static class FakeMainController extends MainController {
        boolean exerciseTableShown = false;
        Workout lastWorkoutShown = null;

        @Override
        public void showExerciseTable(Workout workout) {
            this.exerciseTableShown = true;
            this.lastWorkoutShown = workout;
        }

        // These are not used in this test, but must exist to satisfy abstract class
        @Override public void showWorkoutForm() { /* no-op */ }
        @Override public void showWorkoutTable() { /* no-op */ }
    }

    /** Fake WorkoutService with in-memory storage and no IO. */
    private static class FakeWorkoutService extends WorkoutService {
        private final WorkoutTracker tracker = new WorkoutTracker();

        FakeWorkoutService() {
            super(new TestWorkoutClient());
            resetWithDefaults();
        }

        void resetWithDefaults() {
            tracker.getAllWorkouts().clear();

            Workout w1 = new Workout("Gym A", "Notes Gym A", LocalDate.now(), 1.0,
                    List.of(new Exercise("Warmup", 10, 0.0, "ex-a1")));
            w1.setId(1L);

            Workout w2 = new Workout("Gym B", "Notes Gym B", LocalDate.now(), 1.0,
                    List.of(new Exercise("Warmup", 5, 20.0, "ex-b1")));
            w2.setId(2L);

            tracker.addWorkout(w1);
            tracker.addWorkout(w2);
        }

        @Override
        public WorkoutTracker getWorkoutTracker() {
            return tracker;
        }

        public void deleteWorkout(Workout workout) {
            tracker.getAllWorkouts().remove(workout);
        }
    }

    /** Minimal client for satisfying super calls; does nothing.
     * 
     * <p>
     * This client does nothing and is used to satisfy the super calls in the FakeWorkoutService.
     * </p>
     */
    private static class TestWorkoutClient implements core.service.WorkoutClient {
        @Override public java.util.List<Workout> getAllWorkouts() { return java.util.List.of(); }
        @Override public Workout createWorkout(Workout workout) { return workout; }
        @Override public Workout updateWorkout(Long id, Workout workout) { return workout; }
        @Override public void deleteWorkout(Long id) { /* no-op */ }
    }
    
    /**
     * Confirms the deletion dialog by pressing ENTER (defaults to OK).
     * Keeps tests independent of button text and locale.
     */
    private void confirmDialogOk() {
        WaitForAsyncUtils.waitForFxEvents();
        type(javafx.scene.input.KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Cancels the deletion dialog by pressing ESCAPE (defaults to Cancel).
     */
    private void cancelDialog() {
        WaitForAsyncUtils.waitForFxEvents();
        type(javafx.scene.input.KeyCode.ESCAPE);
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Ensures that clicking the delete button with a single selected workout
     * shows a confirmation dialog and, when confirmed (OK), removes the item
     * from the table (and backing store).
     */
    @Test
    void handleDeleteWorkout_deletes_single_selection_on_confirm() {
        TableView<Workout> table = lookup("#workoutTable").queryTableView();
        // Select first row
        interact(() -> table.getSelectionModel().select(0));
        int before = table.getItems().size();

        // Click delete and confirm
        clickOn("#deleteWorkoutButton");
        confirmDialogOk();

        assertEquals(before - 1, table.getItems().size(), "Selected workout should be deleted");
    }

    /**
     * Verifies that if the user cancels the confirmation dialog,
     * no workouts are removed.
     */
    @Test
    void handleDeleteWorkout_does_not_delete_on_cancel() {
        TableView<Workout> table = lookup("#workoutTable").queryTableView();
        interact(() -> table.getSelectionModel().select(0));
        int before = table.getItems().size();

        // Click delete and cancel
        clickOn("#deleteWorkoutButton");
        cancelDialog();

        assertEquals(before, table.getItems().size(), "No workout should be deleted on cancel");
    }

    /**
     * When the selection contains a workout without an ID, the controller
     * shows a warning and aborts the delete operation. Nothing should be deleted.
     */
    @Test
    void handleDeleteWorkout_shows_warning_and_aborts_when_selection_has_no_id() {
        TableView<Workout> table = lookup("#workoutTable").queryTableView();

        // Add a workout without ID to trigger the "cannot delete unsaved" branch
        Workout unsaved = new Workout(
                "Unsaved", "No id yet", LocalDate.now(), 1.0,
                List.of(new Exercise("Warmup", 5, 0.0, "tmp-1"))
        );
        interact(() -> workoutService.getWorkoutTracker().addWorkout(unsaved));
        WaitForAsyncUtils.waitForFxEvents();

        int before = table.getItems().size();

        // Select ONLY the unsaved workout and attempt to delete
        interact(() -> table.getSelectionModel().clearAndSelect(table.getItems().indexOf(unsaved)));
        clickOn("#deleteWorkoutButton");

        // The controller shows a WARNING alert with only an acknowledge button (OK).
        // Acknowledge it so the test can proceed.
        confirmDialogOk();

        // Nothing should have been deleted
        assertEquals(before, table.getItems().size(), "Delete should abort when selection contains workouts without ID");
        assertTrue(table.getItems().contains(unsaved), "Unsaved workout should still be present");
    }
}
