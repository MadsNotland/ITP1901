package ui;
import static javafx.scene.control.Alert.AlertType.ERROR;

import java.io.IOException;
import java.time.LocalDate;

import core.Workout;
import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui.base.BaseController;

/**
 * Controller class for the {@code WorkoutFormView.fxml} view.
 * <p>
 * This controller manages the user interface for adding new workouts. 
 * Upon initialization, it configures the hour and minute {@link javafx.scene.control.ComboBox} components 
 * used to represent workout duration.
 * </p>
 * <p>
 * Its primary responsibility is to:
 * <ul>
 *   <li>Read and validate user input from the form fields</li>
 *   <li>Create a new {@link core.Workout} instance if the input is valid</li>
 *   <li>Append the created workout to the shared {@link core.WorkoutTracker} list</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutTableController
 * @see core.Workout
 * @see core.WorkoutTracker
 */

public class WorkoutFormController extends BaseController {

    // Input components
    @FXML
    private ComboBox<Integer> comboBoxHours;
    @FXML
    private ComboBox<Integer> comboBoxMinutes;
    @FXML
    private TextField typeField;
    @FXML
    private TextArea notesField;
    @FXML
    private DatePicker datePicker;

    // Core dependencies (injected by MainController)
    private WorkoutTracker workoutTracker;
    private MainController mainController;

    @FXML
    void initialize() {
        initializeComboBoxes();
    }

    /**
     * Injects a reference to the {@link MainController}, allowing this controller 
     * to request navigation actions (e.g., switching back to the workout table view).
     * <p>
     * This method is called manually by the {@link MainController} after the FXML 
     * has been loaded, since the FXMLLoader does not support constructor injection.
     * </p>
     *
     * @param mainController the instance of {@link MainController} that manages view navigation
     */
    public void setMainController (MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Injects the shared {@link WorkoutTracker} instance used to store and manage
     * all workouts in the application.
     * <p>
     * This reference is provided by the {@link MainController} after FXML loading,
     * ensuring that all controllers operate on the same underlying data model.
     * </p>
     *
     * @param workoutTracker the shared {@link WorkoutTracker} instance
     */
    public void setWorkoutTracker(WorkoutTracker workoutTracker) {
        this.workoutTracker = workoutTracker;
    }

    /**
     * Handles the Add Workout button click.
     * Validates the current input fields and, if valid, creates and appends a
     * new {@link Workout} to the tracker. Any validation problem results in a
     * user-facing alert.
     */
    @FXML
    private void handleAddWorkout() throws IOException {
        try {
            // Extract input fields
            String type = typeField.getText();
            String notes = notesField.getText();
            LocalDate date = datePicker.getValue();

            // Validate input fields
            if (!validWorkoutData(type, notes, date)) {
                return;
            }
            // Time must be validated before input. NOTE: minutes is divided by 100 for visual representation
            double time = comboBoxHours.getValue() + (comboBoxMinutes.getValue() / 100.0);

            // Create and add new workout
            Workout newWorkout = new Workout(type, notes, date, time);
            workoutTracker.addWorkout(newWorkout);

            // Navigate back to Table-View
            mainController.showWorkoutTable();

        } 
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches to workout-table if cancel-button is pressed.
     * 
     * @throws IOException
     */
    @FXML
    private void handleCancel() throws IOException {
        mainController.showWorkoutTable();
    }

    /**
     * Populates the hour and minute combo boxes with selectable values.
     */
    private void initializeComboBoxes() {
        comboBoxHours.getItems().clear();
        for (int hours = 0; hours <= 10; hours++) {
            comboBoxHours.getItems().add(hours);
        }
        comboBoxMinutes.getItems().clear();
        for (int minutes = 0; minutes < 60; minutes += 5) {
            comboBoxMinutes.getItems().add(minutes);
        }
    }

    /**
     * Validates user input for a new workout. If validation fails, an alert is
     * shown to the user.
     *
     * @param type  workout type entered in the text field
     * @param notes notes entered in the notes area
     * @param date  date selected in the date picker
     * @return true if all inputs are valid; false otherwise
     */
    private boolean validWorkoutData(String type, String notes, LocalDate date) {
        if (type == null || type.trim().isEmpty()) {
            showAlert(ERROR, "Invalid Input", "Type must not be empty");
            return false;
        }
        if (date == null) {
            showAlert(ERROR, "Invalid Input", "Date must not be empty");
            return false;
        }
        if (notes == null || notes.trim().length() < 3) {
            showAlert(ERROR, "Invalid Input", "Notes must be at least 3 characters long.");
            return false;
        }
        if (comboBoxHours.getValue() == null || comboBoxMinutes.getValue() == null || comboBoxHours.getValue() + comboBoxMinutes.getValue() == 0) {
            showAlert(ERROR, "Invalid Input", "Please select both hours and minutes.");
            return false;
        }
        return true;
    }
}   
