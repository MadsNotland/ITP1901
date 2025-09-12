package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Empty controller - ready for your implementation
 */
public class AppController {

    @FXML
    private ComboBox<Integer> comboBoxHours;
    @FXML
    private ComboBox<Integer> comboBoxMinutes;


    @FXML
    void initialize() {
        initializeComboBoxes();
    }


    private void initializeComboBoxes() {
        comboBoxHours.getItems().clear();
        for (int hours = 0; hours <= 10; hours++) {
            comboBoxHours.getItems().add(hours);
        }

        comboBoxMinutes.getItems().clear();
        for (int minutes = 0; minutes < 60; minutes = minutes += 5){
            comboBoxMinutes.getItems().add(minutes);
        }

        
    }
}
