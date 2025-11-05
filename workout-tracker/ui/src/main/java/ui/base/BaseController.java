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
    protected void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog and returns whether the user confirmed it.
     * <p>
     * This provides a consistent way for all controllers to show confirmation dialogs
     * (e.g., before deleting or resetting data). The dialog uses the standard JavaFX
     * {@link Alert.AlertType#CONFIRMATION}.
     * </p>
     *
     * @param title   the title of the dialog window
     * @param header  the header text (may be {@code null})
     * @param content the main message shown to the user
     * @return {@code true} if the user clicked OK, {@code false} otherwise
     */
    protected boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        var result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }
}
