module app {
    requires javafx.controls;
    requires javafx.fxml;

    opens ui to javafx.graphics, javafx.fxml;
    opens core to javafx.base, javafx.graphics, javafx.fxml;
}
