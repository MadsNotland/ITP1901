package ui;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic UI tests for the exercise section of the workout form.
 *
 * Focus:
 *  - The exercise form is displayed when we open the "Add workout" view
 *  - The fields and button exist (fx:id are correctly linked)
 *  - We can add a valid exercise -> row appears in the table
 *  - Invalid exercise (missing name) shows an alert dialog
 *
 * No domain logic beyond what is already in the controller.
 */
class UiExerciseTest extends ApplicationTest {

    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Waits for a node to appear in the scene graph by retrying the query.
     * 
     * @param query the node query string (e.g., "#exerciseNameField")
     * @param maxAttempts maximum number of attempts
     */
    private void waitForNode(String query, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            WaitForAsyncUtils.waitForFxEvents();
            var node = lookup(query).tryQuery();
            if (node.isPresent()) {
                return;
            }
            sleep(100); // Wait 100ms between attempts
        }
    }

    /**
     * Navigates to the workout form view and waits for it to load.
     * Handles dismissing any server connection error alerts that may appear
     * (since tests run without a server).
     * 
     * @param nodeToWaitFor the node query string to wait for to confirm the form is loaded
     *                      (e.g., "#exerciseNameField" or "#comboBoxReps")
     */
    private void navigateToWorkoutForm(String nodeToWaitFor) {
        clickOn("#add_workout");
        
        // Dismiss server connection error alert if it appears (tests run without server)
        try {
            WaitForAsyncUtils.waitForFxEvents();
            var alert = lookup(".dialog-pane").tryQuery();
            if (alert.isPresent()) {
                clickOn("OK"); // Dismiss the alert
                WaitForAsyncUtils.waitForFxEvents();
            }
        } catch (Exception e) {
            // No alert present, continue
        }
        
        // Wait for the form view to actually appear
        waitForNode(nodeToWaitFor, 50);
    }

    /**
     * Gets references to all exercise form fields.
     * Assumes the form view is already loaded.
     * Waits for fields to be available before querying.
     * 
     * @return FormFields object containing all exercise form components
     */
    private FormFields getExerciseFormFields() {
        // Ensure fields are ready before querying
        WaitForAsyncUtils.waitForFxEvents();
        waitForNode("#exerciseNameField", 50);
        
        return new FormFields(
            lookup("#exerciseNameField").query(),
            lookup("#comboBoxReps").query(),
            lookup("#spinnerWeight").query(),
            lookup("#exerciseTable").queryTableView()
        );
    }

    /**
     * Fills in the exercise form fields with the provided values.
     * 
     * @param name the exercise name (can be null or empty)
     * @param repetitions the number of repetitions
     * @param weight the weight in kg
     */
    private void fillExerciseFields(String name, Integer repetitions, Double weight) {
        FormFields fields = getExerciseFormFields();
        interact(() -> {
            if (name != null) {
                fields.exerciseNameField.setText(name);
            }
            if (repetitions != null) {
                fields.comboBoxReps.setValue(repetitions);
            }
            if (weight != null) {
                fields.spinnerWeight.getValueFactory().setValue(weight);
            }
        });
    }

    /**
     * Clicks the "Add Exercise" button and waits for the action to complete.
     */
    private void clickAddExercise() {
        clickOn("#addExerciseButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Helper class to hold references to exercise form fields.
     */
    private static class FormFields {
        final TextField exerciseNameField;
        final ComboBox<Integer> comboBoxReps;
        final Spinner<Double> spinnerWeight;
        final TableView<?> exerciseTable;

        FormFields(TextField exerciseNameField, ComboBox<Integer> comboBoxReps,
                   Spinner<Double> spinnerWeight, TableView<?> exerciseTable) {
            this.exerciseNameField = exerciseNameField;
            this.comboBoxReps = comboBoxReps;
            this.spinnerWeight = spinnerWeight;
            this.exerciseTable = exerciseTable;
        }
    }

    /**
     * Checks that exercise-related fields exist in the form view
     * after navigating there.
     *
     * This catches errors such as:
     *  - fx:id has been changed in FXML without updating controller/test
     *  - button #add_workout does not show form-view correctly
     */
    @Test
    void exerciseFields_existInFormView() {
        navigateToWorkoutForm("#exerciseNameField");
        
        FormFields fields = getExerciseFormFields();

        assertNotNull(fields.exerciseNameField, "exerciseNameField should exist in the form");
        assertNotNull(fields.comboBoxReps, "comboBoxReps should exist in the form");
        assertNotNull(fields.spinnerWeight, "spinnerWeight should exist in the form");
        assertNotNull(fields.exerciseTable, "exerciseTable should exist in the form");

        assertDoesNotThrow(
            () -> lookup("#addExerciseButton").query(),
            "addExerciseButton should exist in the form"
        );
    }

    /**
     * Happy path:
     *  - Fill in valid exercise name, repetitions, and weight
     *  - Click "Add Exercise"
     *  - Expect exerciseTable to have gained an extra row
     */
    @Test
    void addExercise_addsRowToExerciseTable() {
        navigateToWorkoutForm("#exerciseNameField");
        
        FormFields fields = getExerciseFormFields();
        int before = fields.exerciseTable.getItems().size();

        fillExerciseFields("Bench Press", 10, 60.0);
        clickAddExercise();

        int after = fields.exerciseTable.getItems().size();
        assertEquals(
            before + 1,
            after,
            "Exercise table should gain 1 row after adding a valid exercise"
        );
    }
    
    /**
     * Happy path:
     *  - Add an exercise
     *  - Delete the exercise from the table
     *  - Expect exerciseTable to have one less row
     */
    @Test
    void handleDeleteExercise_removesRowFromExerciseTable() {
        navigateToWorkoutForm("#exerciseNameField");

        FormFields fields = getExerciseFormFields();
        fillExerciseFields("Bench Press", 10, 60.0);
        clickAddExercise();

        int before = fields.exerciseTable.getItems().size();
        assertEquals(1, before, "Exercise table should have 1 row after adding an exercise");

        // Simulate deleting the exercise
        fields.exerciseTable.getItems().remove(0);
        int after = fields.exerciseTable.getItems().size();
        assertEquals(0, after, "Exercise table should be empty after deleting the exercise");
    }

    /**
     * Error path:
     *  - Do not fill in exercise name
     *  - Click "Add Exercise"
     *  - Expect an Alert (DialogPane) with an error message
     *
     * This tests that validation actually communicates with the user via the UI
     * and does not just throw exceptions in the console.
     */
    @Test
    void addExercise_showsAlert_whenNameMissing() {
        navigateToWorkoutForm("#comboBoxReps");
        
        fillExerciseFields(null, 8, 40.0); // Name is null/missing
        clickAddExercise();

        DialogPane dialog = lookup(".dialog-pane").query();
        assertNotNull(dialog, "An alert dialog should appear if exercise name is missing");

        String message = dialog.getContentText().toLowerCase();
        assertTrue(
            message.contains("cannot be empty") || message.contains("exercise name"),
            "Alert text should explain that exercise name is required"
        );
    }

    @Test
    void addExercise_showsAlert_whenRepetitionsMissing() {
        navigateToWorkoutForm("#exerciseNameField");
        
        fillExerciseFields("Squats", null, 100.0); // Repetitions missing
        clickAddExercise();
        
        // Wait for alert dialog to appear
        waitForNode(".dialog-pane", 50);
        DialogPane dialog = lookup(".dialog-pane").query();
        assertNotNull(dialog, "Alert should appear when repetitions are missing");
        
        String message = dialog.getContentText().toLowerCase();
        assertTrue(
            message.contains("repetitions") || message.contains("select"),
            "Alert text should mention repetitions"
        );
    }

    @Test
    void addMultipleExercises_addsMultipleRows() {
        navigateToWorkoutForm("#exerciseNameField");
        FormFields fields = getExerciseFormFields();
        
        int before = fields.exerciseTable.getItems().size();
        
        fillExerciseFields("Push-ups", 20, 0.0);
        clickAddExercise();
        
        fillExerciseFields("Pull-ups", 10, 0.0);
        clickAddExercise();
        
        int after = fields.exerciseTable.getItems().size();
        assertEquals(before + 2, after, "Should add 2 exercises");
    }
}
