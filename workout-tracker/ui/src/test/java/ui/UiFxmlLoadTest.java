package ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Headless FXML load tests to ensure views are well-formed and controllers are creatable.
 * No node lookup or stage showing to avoid flakiness.
 * <p>
 * Verifies that the MainView, WorkoutFormView, and WorkoutTableView FXML files load correctly and that the controllers are created.
 * </p>
 */
class UiFxmlLoadTest {

    @BeforeAll
    static void initToolkit() {
        // Initialize JavaFX toolkit in headless mode using Swing bridge
        new JFXPanel();
    }

    /**
     * Verifies that the MainView FXML file loads correctly and that the controller is created.
     */
    @Test
    void loads_MainView_fxml() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        assertNotNull(root);
        assertNotNull(loader.getController());
    }

    /**
     * Verifies that the WorkoutFormView FXML file loads correctly and that the controller is created.
     */
    @Test
    void loads_WorkoutFormView_fxml() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("WorkoutFormView.fxml"));
        Parent root = loader.load();
        assertNotNull(root);
        assertNotNull(loader.getController());
    }

    /**
     * Verifies that the WorkoutTableView FXML file loads correctly and that the controller is created.
     */
    @Test
    void loads_WorkoutTableView_fxml() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("WorkoutTableView.fxml"));
        Parent root = loader.load();
        assertNotNull(root);
        assertNotNull(loader.getController());
    }
}
