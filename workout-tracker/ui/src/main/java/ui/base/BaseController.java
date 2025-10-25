package ui.base;

import javafx.scene.control.Alert;

/**
 * Abstract base controller that provides common helper methods
 * for all JavaFX controllers in the application.
 *
 * Responsibilities:
 * - Displaying alert dialogs
 * - Simplified FXML loading
 *
 * This class has no FXML of its own.
 */
public abstract class BaseController {

    /**
     * Displays an alert dialog with the given type, title, and message.
     *
     * @param type    the alert type (e.g., ERROR, INFORMATION)
     * @param title   the dialog title
     * @param message the message body
     */
    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
