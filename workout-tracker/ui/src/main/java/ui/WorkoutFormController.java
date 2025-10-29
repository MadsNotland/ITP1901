package ui;

import static javafx.scene.control.Alert.AlertType.ERROR;

import java.io.IOException;
import java.time.LocalDate;

import core.Exercise;
import core.Workout;
import core.WorkoutTracker;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ui.base.BaseController;
import ui.service.WorkoutService;

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
    private ComboBox<Integer> comboBoxReps;
    @FXML
    private TextField typeField;
    @FXML
    private TextArea notesField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Double> spinnerWeight;
    @FXML
    private TableView<Exercise> exerciseTable;
    @FXML
    private TableColumn<Exercise, String> colName;
    @FXML
    private TableColumn<Exercise, Integer> colReps;
    @FXML
    private TableColumn<Exercise, Double> colWeight;
    @FXML
    private TextField exerciseNameField;
    @FXML
    private Button deleteExerciseButton;

    private final ObservableList<Exercise> currentExercises =
        FXCollections.observableArrayList();

    // Core dependencies (injected by MainController)
    private MainController mainController;
    private WorkoutService workoutService;

    @FXML
    void initialize() {
        initializeComboBoxes();
        initializeExerciseTable();
        setupExerciseTableSelectionListeners();

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
     * Injects the shared {@link WorkoutService} instance used to store and manage
     * all workouts in the application via the {@link WorkoutTracker}.
     * <p>
     * This reference is provided by the {@link MainController} after FXML loading,
     * ensuring that all controllers operate on the same underlying data model.
     * </p>
     *
     * @param workoutService the shared {@link WorkoutService} instance
     */
    public void setWorkoutService(WorkoutService workoutService) {
        this.workoutService = workoutService;
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
            if (currentExercises.isEmpty()) {
                showAlert(ERROR, "Invalid Input", "Please add at least one exercise before saving the workout.");
                return;
            }
            // Create and add new workout
            Workout newWorkout = new Workout(type, notes, date, time, currentExercises);
            workoutService.addWorkout(newWorkout);

            // Navigate back to Table-View
            mainController.showWorkoutTable();

        } 
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    /**
     * Handles adding a new exercise to the current workout draft.
     * Validates the exercise input fields (name, repetitions, weight) and enforces
     * a maximum of five exercises. If the input is valid, a new {@link Exercise}
     * is created and added to the observable list backing the exercise table.
     * If validation fails, an alert is shown and the exercise is not added.
     */
    @FXML
    public void handleAddExercise() {
        String exerciseName =  exerciseNameField.getText();
        Integer repetitions = comboBoxReps.getValue();
        Double weight = spinnerWeight.getValue();

        if (!validExerciseData(exerciseName, repetitions, weight)) {
            return;
        }

        try {
            Exercise newExercise = new Exercise(exerciseName, repetitions, weight, null);
            currentExercises.add(newExercise);
        } 
        catch (IllegalArgumentException e) {
            showAlert(ERROR, "Invalid Input", e.getMessage());
        }
    }

    /**
     * Deletes the chosen exercise in the exerciseTable
     */
    @FXML
    public void handleDeleteExercise() {
        Exercise selectedExercise = exerciseTable.getSelectionModel().getSelectedItem();
        if (selectedExercise == null) {
            showAlert(ERROR, "No Selection", "Please select an exercise to delete.");
            return;
        }
        currentExercises.remove(selectedExercise);
        exerciseTable.refresh();
        deleteExerciseButton.setVisible(false);
    }

    /**
     * Sets up all listeners related to user interaction with the exercise table.
     * <p>
     * This includes:
     * <ul>
     *   <li>Displaying and hiding the delete button when a row is selected or deselected</li>
     *   <li>Clearing the selection when the user clicks outside the table (but not on the delete button)</li>
     * </ul>
     * </p>
     * <p>
     * These listeners define how the exercise table responds to selection changes
     * and ensure a smooth, intuitive user experience.
     * </p>
     */
    private void setupExerciseTableSelectionListeners() {
        // Activates the delete button when an exercise is chosen in the table
        exerciseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showDeleteButton();
            }
            else {
                hideDeleteButton();
            }
        });

        // Click outside of table resets choice
        exerciseTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                    if (!exerciseTable.isHover() && (deleteExerciseButton == null || !deleteExerciseButton.isHover())) {
                        exerciseTable.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    /* Shows deleteExersiceButton relative to where the selected exercise is */
    private void showDeleteButton() {
        int index = exerciseTable.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            hideDeleteButton();
            return;
        }

        // Calculates how high we need to move the button in relation to the table
        double rowHeight = 30;  // adjust to height for rows in css
        double headerHeight = 28; // height of tableheader
        double topPadding = 5;    // little air over first row

        // Total vertical displacement
        double translateY = topPadding + headerHeight + (index * rowHeight);

        deleteExerciseButton.setVisible(true);
        deleteExerciseButton.setManaged(true);
        deleteExerciseButton.setTranslateY(translateY);
    }

    /* Hides the deleteExersiceButton when an exercise is not selected */
    private void hideDeleteButton() {
        deleteExerciseButton.setVisible(false);
        deleteExerciseButton.setManaged(false);
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
        for (int hours = 0; hours <= UiConstants.MAX_HOURS; hours++) {
            comboBoxHours.getItems().add(hours);
        }
        comboBoxMinutes.getItems().clear();
        for (int minutes = 0; minutes < 60; minutes += UiConstants.MINUTES_STEP) {
            comboBoxMinutes.getItems().add(minutes);
        }
        comboBoxReps.getItems().clear();
        for (int i = 1; i <= UiConstants.MAX_REPS; i++) {
            comboBoxReps.getItems().add(i);
        }
        SpinnerValueFactory.DoubleSpinnerValueFactory weightFactory =
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, UiConstants.MAX_WEIGHT, 0, UiConstants.WEIGHT_STEP);
        spinnerWeight.setValueFactory(weightFactory);
    }

    /**
     * Configures the exercise table and binds it to the current list of exercises.
     * Defines how each column retrieves its value from the {@link Exercise} model
     * and ensures the table updates automatically when the list changes.
     *
     * Sets a constrained resize policy for consistent layout and a placeholder
     * message when no exercises are present.
     */
    private void initializeExerciseTable() {
        colName.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getName())
        );
        colReps.setCellValueFactory(cell ->
            new SimpleIntegerProperty(cell.getValue().getRepetitions()).asObject()
        );
        colWeight.setCellValueFactory(cell ->
            new SimpleDoubleProperty(cell.getValue().getWeight()).asObject()
        );

        // Bind the table to the list
        exerciseTable.setItems(currentExercises);

        exerciseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        exerciseTable.setPlaceholder(new Label("No exercises added yet"));
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
        if (notes == null || notes.trim().length() < UiConstants.MIN_NOTE_LENGTH) {
            showAlert(ERROR, "Invalid Input", "Notes must be at least 3 characters long.");
            return false;
        }
        else if (notes.trim().length() > UiConstants.MAX_NOTE_LENGTH) {
            showAlert(ERROR, "Invalid Input", "Notes must be less than" + UiConstants.MAX_NOTE_LENGTH + "characters long");
            return false;
        }
        if (comboBoxHours.getValue() == null || comboBoxMinutes.getValue() == null || comboBoxHours.getValue() + comboBoxMinutes.getValue() == 0) {
            showAlert(ERROR, "Invalid Input", "Please select both hours and minutes.");
            return false;
        }
        return true;
    }


    /**
     * Validates user input for a new Exercises. If validation fails, an alert is
     * shown to the user.
     *
     * @param exerciseName name of exercise entered in the text field
     * @param repetitions repetitions entered in the repetitions area
     * @param weight weight selected in the weight picker
     * @return true if all inputs are valid; false otherwise
     */
    private boolean validExerciseData(String exerciseName, Integer repetitions, Double weight) {
        if (exerciseName == null || exerciseName.isBlank()) {
            showAlert(ERROR, "Invalid Input", "Exercise name cannot be empty.");
            return false;
        }
        if (repetitions == null) {
            showAlert(ERROR, "Invalid Input", "Please select repetitions.");
            return false;
        }
        if (weight == null || weight < 0) {
            showAlert(ERROR, "Invalid Input", "Weight must be 0 or more.");
            return false;
        }
        if (currentExercises.size() >= UiConstants.MAX_EXERCISES_PER_WORKOUT) {
            showAlert(ERROR, "Limit reached", "You can only add up to 5 exercises.");
            return false;
        }
        return true;
    }
}   
