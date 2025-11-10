package ui;
import java.io.IOException;
import java.time.LocalDate;

import core.Workout;
import core.service.WorkoutService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import ui.base.BaseController;

/**
 * Controller class for the {@code WorkoutTableView.fxml} view.
 * <p>
 * Displays all recorded workouts in a table and provides controls
 * for adding and saving workouts. This view serves as the main 
 * overview screen of the application.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Bind {@link core.Workout} data to the table columns</li>
 *   <li>Delegate navigation to {@link MainController}</li>
 *   <li>Persist workout data via {@link core.service.WorkoutService}</li>
 * </ul>
 * </p>
 *
 * @see ui.WorkoutFormController
 * @see core.Workout
 * @see core.service.WorkoutService
 */
public class WorkoutTableController extends BaseController {

    // Table view components
    @FXML
    private TableView<Workout> workoutTable;
    @FXML
    private TableColumn<Workout, String> typeColumn;
    @FXML
    private TableColumn<Workout, LocalDate> dateColumn;
    @FXML
    private TableColumn<Workout, Double> timeColumn;
    @FXML
    private TableColumn<Workout, String> notesColumn;
    @FXML 
    private Button deleteWorkoutButton;
    @FXML
    private javafx.scene.layout.StackPane deleteButtonSlot;

    // Core dependencies (injected by MainController)
    private MainController mainController;
    private WorkoutService workoutService;

    /**
     * Initializes the table view once dependencies have been injected.
     * Called manually by {@link MainController} after FXML loading.
     */
    @FXML
    void initializeData() {
        setTableView();
        setupWorkoutTableSelectionListener();
        setupDeleteButtonBehavior();
    }

    /**
     * Injects the {@link MainController} to enable view navigation.
     *
     * @param mainController reference to the parent {@link MainController}
     */
    public void setMainController(MainController mainController) {
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
     * @param workoutService
     */
    public void setWorkoutService(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }
    
    /**
     * Navigates to the workout form view when the user presses the "Add" button.
     *
     * @throws IOException if the form view cannot be loaded
     */
    @FXML
    private void handleAddButton() throws IOException {
        mainController.showWorkoutForm();
    }

    /**
     * Loads workouts from the server via {@link WorkoutService}
     * and handles any potential IO exceptions.
     * <p>
     * Handles the click event for a row in the workout table.
     * </p>
     * @param event the mouse event
     * @throws IOException if the workout form cannot be loaded
     */
    @FXML
    private void handleRowClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Workout selected = workoutTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                mainController.showExerciseTable(selected);
            }
        } 
        else {
            // Position delete button when row is clicked (single click)
            positionDeleteButtonForSelectedRow();
        }
    }

    /**
     * Deletes the selected workout(s) after user confirmation.
     * <p>
     * Deletes the selected workouts from the workout table.
     * </p>
     */
    @FXML
    private void handleDeleteWorkout() {
        var selectedItems = workoutTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }

        // Check if any selected workouts don't have IDs
        var workoutsWithoutId = selectedItems.stream()
            .filter(w -> w.getId() == null)
            .toList();
        
        if (!workoutsWithoutId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                "Cannot delete",
                "%d workout(s) haven't been saved to the server yet, so they have no ID.%nSave them first, then try deleting."
                    .formatted(workoutsWithoutId.size()));
            return;
        }

        // Build confirmation message
        String confirmationMessage;
        if (selectedItems.size() == 1) {
            Workout selected = selectedItems.get(0);
            confirmationMessage = "Type: %s%nDate: %s%nTime: %s%n%nThis cannot be undone."
                .formatted(selected.getType(), selected.getDate(), selected.getTime());
        } 
        else {
            confirmationMessage = "You are about to delete %d workouts.%n%nThis cannot be undone."
                .formatted(selectedItems.size());
        }

        boolean confirmed = showConfirmation(
            selectedItems.size() == 1 ? "Delete workout" : "Delete workouts",
            selectedItems.size() == 1 
                ? "Are you sure you want to delete this workout?"
                : "Are you sure you want to delete these workouts?",
            confirmationMessage
        );
        if (!confirmed) {
            return;
        }

        // Create a copy of selected items to avoid modification during iteration
        var workoutsToDelete = new java.util.ArrayList<>(selectedItems);
        
        // Delete all selected workouts
        var deleted = new java.util.ArrayList<Workout>();
        var errors = new java.util.ArrayList<String>();
        
        for (Workout workout : workoutsToDelete) {
            try {
                workoutService.deleteWorkout(workout);
                deleted.add(workout);
            } 
            catch (IllegalArgumentException iae) {
                errors.add("Failed to delete %s (%s): %s"
                    .formatted(workout.getType(), workout.getDate(), iae.getMessage()));
            }
            catch (IOException ioe) {
                errors.add("Network error deleting %s (%s): %s"
                    .formatted(workout.getType(), workout.getDate(), ioe.getMessage()));
            }
        }

        // Show results
        if (!errors.isEmpty()) {
            String errorMessage = String.join("%n", errors);
            if (deleted.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Delete failed", errorMessage);
            } 
            else {
                showAlert(Alert.AlertType.WARNING,
                    "Partial delete",
                    "Deleted %d workout(s), but %d failed:%n%n%s"
                        .formatted(deleted.size(), errors.size(), errorMessage));
            }
        }

        // Clear selection after deletion to prevent auto-selection of next item
        workoutTable.getSelectionModel().clearSelection();
    }

    /**
     * Binds table columns to {@link Workout} properties and connects
     * the table to the observable list of workouts.
     */
    private void setTableView() {
        workoutTable.setItems(workoutService.getWorkoutTracker().getAllWorkouts());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        // Prevent extra empty column from appearing when table expands
        workoutTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Enable multiple selection mode
        workoutTable.getSelectionModel().setSelectionMode(
            javafx.scene.control.SelectionMode.MULTIPLE
        );
    }

    /**
     * Sets up a listener for user interactions with the workout table.
     * <p>
     * This listener detects when the user clicks outside the {@link #workoutTable}
     * and clears any existing selection. It improves the overall user experience
     * by preventing a row from remaining highlighted when the user interacts with
     * other parts of the view.
     * </p>
     * <p>
     * The listener is attached dynamically once the table’s {@link javafx.scene.Scene}
     * becomes available, ensuring proper event handling after the FXML view is loaded.
     * </p>
     */
    private void setupWorkoutTableSelectionListener() {
        workoutTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
                    javafx.scene.Node node = (javafx.scene.Node) e.getTarget();
                    boolean inside = false;
                    while (node != null) {
                        if (node == workoutTable || node == deleteWorkoutButton || node == deleteButtonSlot) {
                            inside = true;
                            break;
                        }
                        node = node.getParent();
                    }
                    if (!inside) {
                        workoutTable.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    /**
     * Displays a simple message for the Info-Button.
     * <p>
     * Shows a guide for the workout tracker functionality.
     * </p>
     */
    @FXML
    private void handleInfo() {
        String infoText = """
                     Workout Tracker App Guide
            
            • Add a workout: Click "Add Workout" button.
            • View the workout: Doubleclick a workout-row.
            • Delete multiple workouts: Ctrl+Click
            
            • Click outside the table to clear a selection.
            • Each workout is saved automatically.
            """;

        showAlert(javafx.scene.control.Alert.AlertType.INFORMATION,
            "About Workout Tracker", infoText);
    }

    /**
     * Positions the delete button vertically aligned with the first selected row.
     * When multiple rows are selected, positions the button at the first selected row.
     * Calculates the row's Y position relative to the table and translates the button slot accordingly.
     */
    private void positionDeleteButtonForSelectedRow() {
        var selectedItems = workoutTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            deleteWorkoutButton.setVisible(false);
            deleteButtonSlot.setTranslateY(0);
            return;
        }
        
        // Use the first selected item's index for positioning
        int selectedIndex = workoutTable.getItems().indexOf(selectedItems.get(0));
        if (selectedIndex < 0) {
            deleteWorkoutButton.setVisible(false);
            deleteButtonSlot.setTranslateY(0);
            return;
        }
        
        // Use Platform.runLater to ensure layout is complete before calculating positions
        javafx.application.Platform.runLater(() -> {
            // Verify selection still exists and first item hasn't changed
            var currentSelected = workoutTable.getSelectionModel().getSelectedItems();
            if (!currentSelected.isEmpty() && 
                workoutTable.getItems().indexOf(currentSelected.get(0)) == selectedIndex) {
                positionButtonNow();
            }
        });
    }
    
    /**
     * Helper method that performs the actual button positioning calculation.
     * Uses scene coordinates to account for scrolling.
     * Positions button at the first selected row when multiple rows are selected.
     */
    private void positionButtonNow() {
        var selectedItems = workoutTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        
        // Use the first selected item's index for positioning
        int selectedIndex = workoutTable.getItems().indexOf(selectedItems.get(0));
        if (selectedIndex < 0) {
            return;
        }
        
        // Find the TableRow for the selected index
        javafx.scene.control.TableRow<Workout> selectedRow = null;
        var rowCells = workoutTable.lookupAll(".table-row-cell");
        for (var node : rowCells) {
            if (node instanceof javafx.scene.control.TableRow) {
                @SuppressWarnings("unchecked")
                javafx.scene.control.TableRow<Workout> tableRow = 
                    (javafx.scene.control.TableRow<Workout>) node;
                if (tableRow.getIndex() == selectedIndex && !tableRow.isEmpty()) {
                    selectedRow = tableRow;
                    break;
                }
            }
        }
        
        if (selectedRow != null) {
            // Get the row's bounds relative to the table (accounts for scrolling automatically)
            javafx.geometry.Bounds rowBounds = selectedRow.getBoundsInParent();
            
            // Get the table header height
            javafx.scene.Node header = workoutTable.lookup(".column-header-background");
            double tableHeaderHeight = header != null ? header.getBoundsInLocal().getHeight() : 30.0;
            
            // Check if the row is visible in the viewport
            // rowBounds.getMinY() is relative to VirtualFlow content area (starts at 0 below header)
            // If row is scrolled above viewport, getMinY() will be negative
            // If row is scrolled below viewport, getMinY() will be >= visible height
            double visibleHeight = workoutTable.getHeight() - tableHeaderHeight;
            boolean isRowVisible = rowBounds.getMinY() >= 0 && rowBounds.getMinY() < visibleHeight 
                && rowBounds.getMaxY() > 0 && rowBounds.getMaxY() < visibleHeight;
            
            if (!isRowVisible) {
                // Row is not visible in viewport, hide the button
                deleteWorkoutButton.setVisible(false);
                return;
            }
            
            // Calculate the row's center Y position relative to the table's top
            // rowBounds.getMinY() is relative to the VirtualFlow content area (below header)
            // but we need it relative to the table's top including header
            double rowCenterY = rowBounds.getMinY() + (rowBounds.getHeight() / 2);
            
            // Since both table and buttonSlot are in the same HBox (inner HBox),
            // we need to add the header height to position relative to table top
            double buttonSlotY = tableHeaderHeight + rowCenterY - (deleteButtonSlot.getHeight() / 2);
            
            // Set the translation relative to the button slot's parent (inner HBox)
            deleteButtonSlot.setTranslateY(buttonSlotY);
            deleteWorkoutButton.setVisible(true);
        } 
        else {
            // Fallback: use a simpler calculation based on row index
            // This fallback doesn't account for scrolling, but is better than nothing
            double rowHeight = workoutTable.getFixedCellSize() > 0 
                ? workoutTable.getFixedCellSize() 
                : 32.0; // Default row height from CSS
            double tableHeaderHeight = 30.0;
            double rowY = selectedIndex * rowHeight;
            deleteButtonSlot.setTranslateY(rowY + tableHeaderHeight + (rowHeight / 2) - (deleteButtonSlot.getHeight() / 2));
            deleteWorkoutButton.setVisible(true);
        }
    }

    /**
     * Sets up the delete button's initial state and its dynamic visibility behavior.
     * <p>
     * This method ensures the delete button is hidden by default and becomes visible
     * only when a workout row is selected in the table. It also listens for selection
     * changes and scroll events to reposition the delete button vertically, aligning it
     * with the selected row. When no row is selected, the button is hidden and reset
     * to its default position.
     * </p>
     * <p>
     * Extracted for readability to keep {@link #initializeData()} focused on initialization flow
     * rather than UI layout logic.
     * </p>
     */
    private void setupDeleteButtonBehavior() {
        // Initially hide the delete button
        deleteWorkoutButton.setVisible(false);
        deleteButtonSlot.setManaged(true);
        deleteWorkoutButton.setManaged(true);
        
        // Prevent HBox from managing the StackPane's vertical position
        // We'll manage it manually via translateY
        deleteButtonSlot.setManaged(true);
        
        // Position delete button next to selected row(s) when selection changes
        workoutTable.getSelectionModel().getSelectedItems().addListener(
            (javafx.collections.ListChangeListener<Workout>) change -> {
                if (!workoutTable.getSelectionModel().getSelectedItems().isEmpty()) {
                    positionDeleteButtonForSelectedRow();
                } 
                else {
                    deleteWorkoutButton.setVisible(false);
                    deleteButtonSlot.setTranslateY(0);
                }
            }
        );
        
        // Reposition button when table is scrolled (so it follows the selected row)
        workoutTable.setOnScroll(e -> {
            if (!workoutTable.getSelectionModel().getSelectedItems().isEmpty()) {
                // Use Platform.runLater to ensure layout is updated after scroll
                javafx.application.Platform.runLater(() -> {
                    if (!workoutTable.getSelectionModel().getSelectedItems().isEmpty()) {
                        positionDeleteButtonForSelectedRow();
                    }
                });
            }
        });
        
        // Also listen for scroll bar changes (when user drags scrollbar)
        workoutTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Wait for scene to be fully laid out before finding scrollbar
                javafx.application.Platform.runLater(() -> {
                    // Find the vertical scrollbar and listen for value changes
                    javafx.scene.control.ScrollBar vScrollBar = (javafx.scene.control.ScrollBar) 
                        workoutTable.lookup(".scroll-bar:vertical");
                    if (vScrollBar != null) {
                        vScrollBar.valueProperty().addListener((scrollObs, oldVal, newVal) -> {
                            if (!workoutTable.getSelectionModel().getSelectedItems().isEmpty()) {
                                // Use Platform.runLater to ensure layout is updated after scroll
                                javafx.application.Platform.runLater(() -> {
                                    if (!workoutTable.getSelectionModel().getSelectedItems().isEmpty()) {
                                        positionDeleteButtonForSelectedRow();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
