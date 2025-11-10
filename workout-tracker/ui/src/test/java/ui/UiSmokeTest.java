package ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Minimal UI smoke test to ensure the main JavaFX view loads and shows.
 * This avoids brittle node lookups and end-to-end flows that vary by environment.
 */
class UiSmokeTest extends ApplicationTest {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        this.primaryStage = stage;
    }

    @Test
    void primaryStage_isShowing_andHasScene() {
        assertNotNull(primaryStage, "Primary stage should be initialized");
        assertTrue(primaryStage.isShowing(), "Primary stage should be showing");
        assertNotNull(primaryStage.getScene(), "Primary stage should have a scene");
        assertNotNull(primaryStage.getScene().getRoot(), "Scene should have a root node");
    }
}
