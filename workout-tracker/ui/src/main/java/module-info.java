module workout.tracker.ui {
    // Dependencies on internal modules
    // Use 'transitive' so clients of this module can access core types from exported packages
    requires transitive workout.tracker.core;

    // JavaFX dependencies
    // Use 'transitive' so clients of this module can access JavaFX types from exported packages
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    // Stage lives in javafx.graphics (referenced in WorkoutTrackerApp.java)
    requires transitive javafx.graphics;
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
