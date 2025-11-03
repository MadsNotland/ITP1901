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
     * Checks that exercise-related fields exist in the form view
     * after navigating there.
     *
     * This catches errors such as:
     *  - fx:id has been changed in FXML without updating controller/test
     *  - button #add_workout does not show form-view correctly
     */
    @Test
    void exerciseFields_existInFormView() {
        clickOn("#add_workout");
        WaitForAsyncUtils.waitForFxEvents(); // Wait for view navigation to complete

        TextField exerciseNameField = lookup("#exerciseNameField").query();
        ComboBox<Integer> comboBoxReps = lookup("#comboBoxReps").query();
        Spinner<Double> spinnerWeight = lookup("#spinnerWeight").query();
        TableView<?> exerciseTable = lookup("#exerciseTable").queryTableView();

        assertNotNull(exerciseNameField, "exerciseNameField should exist in the form");
        assertNotNull(comboBoxReps, "comboBoxReps should exist in the form");
        assertNotNull(spinnerWeight, "spinnerWeight should exist in the form");
        assertNotNull(exerciseTable, "exerciseTable should exist in the form");

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
        clickOn("#add_workout");
        WaitForAsyncUtils.waitForFxEvents(); // Wait for view navigation to complete

        TextField exerciseNameField = lookup("#exerciseNameField").query();
        ComboBox<Integer> comboBoxReps = lookup("#comboBoxReps").query();
        Spinner<Double> spinnerWeight = lookup("#spinnerWeight").query();
        TableView<?> exerciseTable = lookup("#exerciseTable").queryTableView();

        int before = exerciseTable.getItems().size();

        interact(() -> {
            exerciseNameField.setText("Bench Press");
            comboBoxReps.setValue(10);
            spinnerWeight.getValueFactory().setValue(60.0);
        });

        clickOn("#addExerciseButton");

        int after = exerciseTable.getItems().size();

        assertEquals(
            before + 1,
            after,
            "Exercise table should gain 1 row after adding a valid exercise"
        );
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
        clickOn("#add_workout");
        WaitForAsyncUtils.waitForFxEvents(); // Wait for view navigation to complete

        ComboBox<Integer> comboBoxReps = lookup("#comboBoxReps").query();
        Spinner<Double> spinnerWeight = lookup("#spinnerWeight").query();

        interact(() -> {
            comboBoxReps.setValue(8);
            spinnerWeight.getValueFactory().setValue(40.0);
        });

        clickOn("#addExerciseButton");
        WaitForAsyncUtils.waitForFxEvents(); // Wait for alert dialog to appear

        DialogPane dialog = lookup(".dialog-pane").query();
        assertNotNull(dialog, "An alert dialog should appear if exercise name is missing");

        String message = dialog.getContentText().toLowerCase();
        assertTrue(
            message.contains("cannot be empty") || message.contains("exercise name"),
            "Alert text should explain that exercise name is required"
        );
    }
}
