package ui.service;

import java.io.IOException;

import core.Workout;
import core.WorkoutTracker;

/**
 * Service class responsible for managing persistence and data flow
 * for {@link Workout} and {@link WorkoutTracker}.
 *
 * <p>This class abstracts away REST API communication and provides a clean
 * interface for controllers (e.g. {@code MainController}) to interact with
 * the business logic without worrying about HTTP details.</p>
 *
 * <p>Design principles:
 * <ul>
 *   <li><b>Single Responsibility:</b> Handles only data operations.</li>
 *   <li><b>Separation of Concerns:</b> Keeps controllers UI-focused.</li>
 *   <li><b>REST Integration:</b> All persistence operations go through the REST API server.</li>
 * </ul>
 * </p>
 */
public class WorkoutService {
    
    private final RestWorkoutClient restClient;
    private final WorkoutTracker workoutTracker;

    /**
     * Creates a {@code WorkoutService} instance that initializes
     * a REST client for server communication.
     */
    public WorkoutService() {
        this.restClient = new RestWorkoutClient();
        this.workoutTracker = new WorkoutTracker();
    }

    /**
     * Loads all workouts from the server via REST API.
     *
     * @throws IOException if the HTTP request fails or the server cannot be reached
     */
    public void loadWorkouts() throws IOException {
        var loadedWorkouts = restClient.getAllWorkouts();
        workoutTracker.getAllWorkouts().setAll(loadedWorkouts);
    }

    /**
     * Saves all workouts currently in the {@link WorkoutTracker} to file.
     *
     * <p>This method is now a no-op since workouts are persisted immediately
     * via REST API when they are created, updated, or deleted. This method is
     * kept for backward compatibility with existing UI code.</p>
     *
     * @throws IOException never thrown (method is a no-op)
     */
    public void saveWorkouts() throws IOException {
        // No-op: workouts are persisted immediately via REST API
    }

    /**
     * Returns the shared {@link WorkoutTracker} instance used by the application.
     *
     * @return the internal {@code WorkoutTracker}
     */
    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    public void addWorkout(Workout workout) {
        workoutTracker.addWorkout(workout);
    }
}
