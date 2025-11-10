package core.service;

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
    
    private final WorkoutClient client;
    private final WorkoutTracker workoutTracker;

    /**
     * Creates a {@code WorkoutService} instance that initializes
     * a REST client for server communication.
     * 
     * <p>This constructor creates a default {@link RestWorkoutClient} instance
     * that connects to http://localhost:8080/api/workouts.</p>
     */
    public WorkoutService() {
        this(new RestWorkoutClient());
    }

    /**
     * Creates a {@code WorkoutService} instance with a provided REST client.
     * 
     * <p>This constructor is primarily used for testing, allowing injection
     * of a mock or test implementation of {@link RestWorkoutClient} to avoid
     * making actual HTTP requests during unit tests.</p>
     * 
     * @param client the REST client to use for server communication
     */
    public WorkoutService(WorkoutClient client) {
        this.client = client;
        this.workoutTracker = new WorkoutTracker();
    }

    /**
     * Loads all workouts from the server via REST API.
     *
     * @throws IOException if the HTTP request fails or the server cannot be reached
     */
    public void loadWorkouts() throws IOException {
        var loadedWorkouts = client.getAllWorkouts();
        workoutTracker.getAllWorkouts().setAll(loadedWorkouts);
    }

    /**
     * Updates an existing workout on the server via REST API.
     *
     * <p>This method sends the updated workout data to the server, which persists
     * the changes immediately. The workout must already exist on the server with
     * the specified ID.</p>
     *
     * @param id the ID of the workout to update
     * @param workout the updated workout data
     * @throws IOException if the HTTP request fails, the server cannot be reached,
     *                     or the workout with the given ID is not found (404)
     * @throws IllegalArgumentException if workout is null
     */
    public void updateWorkout(Long id, Workout workout) throws IOException {
        client.updateWorkout(id, workout);
    }

    /**
     * Returns the shared {@link WorkoutTracker} instance used by the application.
     *
     * @return the internal {@code WorkoutTracker}
     */
    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    /**
     * Creates a new workout on the server and adds it to the tracker.
     *
     * <p>This method persists the workout immediately to the server via REST API.
     * The server assigns an ID to the workout and returns the created workout
     * with the assigned ID. This workout (with ID) is then added to the tracker.</p>
     *
     * @param workout the workout to create (ID is ignored, server will assign one)
     * @throws IOException if the HTTP request fails or the server cannot be reached
     * @throws IllegalArgumentException if workout is null
     */
    public void addWorkout(Workout workout) throws IOException {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }
        
        // Persist to server immediately - server assigns ID
        Workout createdWorkout = client.createWorkout(workout);
        
        // Add the created workout (with server-assigned ID) to tracker
        workoutTracker.addWorkout(createdWorkout);
    }

    /**
     * Deletes a workout from the server and removes it from the tracker.
     *
     * <p>This method deletes the workout immediately from the server via REST API.
     * If the deletion is successful, the workout is also removed from the tracker.</p>
     *
     * @param workout the workout to delete
     * @throws IOException if the HTTP request fails or the server cannot be reached
     * @throws IllegalArgumentException if workout is null or does not exist
     */
    public void deleteWorkout(Workout workout) throws IOException {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }
        client.deleteWorkout(workout.getId());
        workoutTracker.removeWorkoutById(workout.getId());
    }
  
    /**
     * Finds a workout by its ID in the local {@link WorkoutTracker}.
     *
     * <p>This method searches only in the local in-memory cache (WorkoutTracker),
     * not on the server. The workout must have been previously loaded via
     * {@link #loadWorkouts()} or added via {@link #addWorkout(Workout)} to be
     * found by this method.</p>
     *
     * @param workoutId the ID of the workout to find
     * @return the workout with the matching ID, or {@code null} if not found
     *         in the local tracker
     */
    public Workout findWorkout(Long workoutId) {
        if (workoutId == null) {
            return null;
        }
        return getWorkoutTracker()
                .getAllWorkouts()
                .stream()
                .filter(w -> workoutId.equals(w.getId()))
                .findFirst()
                .orElse(null);
    }
}
