module workout.tracker.ui {
    // Avhengigheter til andre moduler
    requires workout.tracker.core;
    requires workout.tracker.persistence;
    
    // JavaFX avhengigheter
    requires javafx.controls;
    requires javafx.fxml;
    
    // Grunnleggende Java
    requires java.base;
    
    // Eksporterer UI-pakken
    exports ui;
    
    // Åpner pakker for JavaFX og testing
    opens ui to javafx.graphics, javafx.fxml, org.junit.jupiter;
}