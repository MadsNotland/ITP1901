package core;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Core class
 */
public class WorkoutTracker {
    private final ObservableList<Workout> workouts;

    public WorkoutTracker() {
        this.workouts = FXCollections.observableArrayList();
    }

    public void addWorkout(Workout workout) {
        if (workout == null) {throw new IllegalArgumentException("Workout cannot be null");}
        workouts.add(0, workout);
    }
    
    public ObservableList<Workout> getAllWorkouts() {
        return workouts;
    }
    public List<Workout> getWorkouts() {
        return new ArrayList<>(workouts);
    }
}