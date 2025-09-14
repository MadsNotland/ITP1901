package core;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manages a collection of workout sessions.
 * Provides functionality to add workouts and retrieve them in different formats.
 * New workouts are added at the beginning of the list (most recent first).
 */
public class WorkoutTracker {
    private final ObservableList<Workout> workouts;

    public WorkoutTracker() {
        this.workouts = FXCollections.observableArrayList();
    }

    public void addWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }
        workouts.add(0, workout);
    }
    
    /**
     * Returns the observable list of all workouts.
     * This list is live and will automatically update the UI when workouts are added or removed.
     * 
     * @return the observable list of workouts
     */
    public ObservableList<Workout> getAllWorkouts() {
        return workouts;
    }
    
    /**
     * Returns a copy of all workouts as a regular List.
     * This method creates a defensive copy, so modifications to the returned list
     * will not affect the internal workout storage.
     * 
     * @return a copy of all workouts as a List
     */
    public List<Workout> getWorkouts() {
        return new ArrayList<>(workouts);
    }
}