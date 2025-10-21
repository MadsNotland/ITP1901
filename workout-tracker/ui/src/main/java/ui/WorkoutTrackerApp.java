package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Workout Tracker JavaFX application.
 * <p>
 * This class bootstraps the UI by loading the {@code WorkoutTrackerView.fxml} layout and
 * showing the primary {@link javafx.stage.Stage}. The JavaFX runtime invokes
 * {@link #start(javafx.stage.Stage)} after initialization.
 * </p>
 */
public class WorkoutTrackerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the main FXML and set up the primary scene
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent, 700, 550));
        stage.show();
    }

    /**
     * Standard Java main method. Delegates to {@link Application#launch(String...)}
     * which initializes the JavaFX runtime and calls {@link #start(Stage)}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
