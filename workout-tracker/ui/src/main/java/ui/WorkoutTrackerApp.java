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

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("WorkoutTrackerView.fxml"));
        Parent parent = fxmlLoader.load();

        // assign the static field instead of creating a local variable
        scene = new Scene(parent, 700, 550);
        stage.setScene(scene);

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WorkoutTrackerApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
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
