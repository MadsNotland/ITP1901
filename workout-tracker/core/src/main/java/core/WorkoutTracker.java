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

    /**
     * Adds a workout at the start of the list.
     *
     * @param workout non-null workout to add
     * @throws IllegalArgumentException if {@code workout} is {@code null}
     */
    public void addWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }
        workouts.add(0, workout);
    }
    
    /**
     * Returns the live observable list of workouts.
     * Changes to this list are reflected in the internal state and any UI bindings.
     *
     * @return the live observable list of workouts
     */
    public ObservableList<Workout> getAllWorkouts() {
        return workouts;
    }
    
    /**
     * Returns a defensive copy of all workouts.
     * Modifying the returned list does not affect the internal storage.
     *
     * @return a copy of all workouts
     */
    public List<Workout> getWorkouts() {
        return new ArrayList<>(workouts);
    }
}
