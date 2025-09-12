package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Core class
 */
public class WorkoutTracker {
    private final List<Workout> workouts;

    public WorkoutTracker() {
        this.workouts = new ArrayList<>();
    }

    public void addWorkout(Workout workout) {
        if (workout == null) {throw new IllegalArgumentException("Workout cannot be null");}
        workouts.add(workout);
    }
    
    public List<Workout> getAllWorkouts() {
        return new ArrayList<>(workouts);
    }
}