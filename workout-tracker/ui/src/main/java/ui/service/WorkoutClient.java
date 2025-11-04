package ui.service;

import java.io.IOException;
import java.util.List;

import core.Workout;

/**
 * Defines the contract for communicating with a workout data source.
 * <p>
 * Implementations of this interface handle the underlying transport or storage
 * mechanism (e.g., REST API, local cache, mock), while the {@code WorkoutService}
 * operates only on this abstraction. This design allows the UI and service layers
 * to remain decoupled from specific client implementations.
 * </p>
 *
 * <p>
 * Each method may throw an {@link IOException} if a communication or I/O error occurs.
 * </p>
 */
public interface WorkoutClient {

    /**
     * Retrieves all workouts from the data source.
     *
     * @return a list of all {@link Workout} objects
     * @throws IOException if an error occurs during data retrieval
     */
    List<Workout> getAllWorkouts() throws IOException;

    /**
     * Creates a new workout entry in the data source.
     *
     * @param workout the {@link Workout} to create
     * @return the created {@link Workout}, possibly including an assigned ID
     * @throws IOException if an error occurs during creation
     */
    Workout createWorkout(Workout workout) throws IOException;
    
    /**
     * Updates an existing workout in the data source.
     *
     * @param id the ID of the workout to update
     * @param workout the updated {@link Workout} data
     * @return the updated {@link Workout}
     * @throws IOException if an error occurs during the update
     */
    Workout updateWorkout(Long id, Workout workout) throws IOException;
    
    /**
     * Deletes a workout from the data source.
     *
     * @param id the ID of the workout to delete
     * @throws IOException if an error occurs during deletion
     */
    void deleteWorkout(Long id) throws IOException;
}
