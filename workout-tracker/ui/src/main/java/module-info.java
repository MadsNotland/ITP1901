module workout.tracker.ui {
    // Dependencies on internal modules
    // Use 'transitive' so clients of this module can access core types from exported packages
    requires transitive workout.tracker.core;
    requires workout.tracker.persistence;

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
    // HTTP client for REST API communication
    requires java.net.http;

    // Jackson for JSON serialization/deserialization in REST client
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    // requires org.junit.jupiter.api;

    // Export UI package to consumers
    exports ui;
    // Export service package since it's used in public API of exported controllers
    exports ui.service;

    // Open package for JavaFX reflection (FXML) and for testing
    opens ui to javafx.graphics, javafx.fxml, org.junit.jupiter;
    opens ui.base to javafx.fxml;
    opens ui.service to javafx.fxml;
}
