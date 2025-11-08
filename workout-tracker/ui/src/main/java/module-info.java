module workout.tracker.ui {
    // Dependencies on internal modules
    requires workout.tracker.core;
    requires workout.tracker.persistence;

    // JavaFX dependencies
    requires javafx.controls;
    requires javafx.fxml;
    // Stage lives in javafx.graphics (referenced in WorkoutTrackerApp.java)
    requires javafx.graphics;
    // Needed for JFXPanel when initializing toolkit in tests
    requires javafx.swing;

    // Base Java module
    requires java.base;
    // Needed when initializing toolkit via JFXPanel in tests
    requires java.desktop;

    // Export UI package to consumers
    exports ui;

    // Open package for JavaFX reflection (FXML) and for testing
    opens ui to javafx.graphics, javafx.fxml, org.junit.jupiter;
    opens ui.base to javafx.fxml;
}
