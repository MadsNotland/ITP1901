package ui;

import java.io.IOException;

import core.WorkoutTracker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class MainController {

    @FXML
    private BorderPane contentArea;
    
    // One common WorkoutTracker for the entire application
    private final WorkoutTracker workoutTracker = new WorkoutTracker();

    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    @FXML
    private void initialize() throws IOException {
        showWorkoutTable();
    }

    public void showWorkoutTable() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutTableView.fxml"));
        Region tableView = loader.load();

        // Get the controller and send references
        WorkoutTableController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkoutTracker(workoutTracker);
        controller.initializeData();

        contentArea.setCenter(tableView);
    }

    public void showWorkoutForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutFormView.fxml"));
        Region formView = loader.load();

        // Get the controller and send references
        WorkoutFormController controller = loader.getController();
        controller.setMainController(this);
        controller.setWorkoutTracker(workoutTracker);

        contentArea.setCenter(formView);
    }
}
