package server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import core.Workout;
import persistence.WorkoutFileManager;

/**
 * Service class for managing workout operations.
 * Handles business logic and integrates with persistence layer.
 */
@Service
public class WorkoutService {
    
    private final WorkoutFileManager fileManager;
    private final AtomicLong nextId = new AtomicLong(1);
    
    public WorkoutService() {
        // Initialize with default data file path
        this.fileManager = new WorkoutFileManager(Path.of("../data/workouts.json"));
        initializeIds();
    }
    
    /**
     * Initialize ID counter based on existing workouts.
     */
    private void initializeIds() {
        try {
            List<Workout> workouts = fileManager.loadWorkouts();
            long maxId = workouts.stream()
                    .map(Workout::getId)
                    .filter(id -> id != null)
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L);
            nextId.set(maxId + 1);
        } 
        catch (IOException e) {
            // If file doesn't exist or can't be read, start with ID 1
            nextId.set(1L);
        }
    }
    
    /**
     * Get all workouts.
     * @return list of all workouts
     */
    public List<Workout> getAllWorkouts() {
        try {
            return fileManager.loadWorkouts();
        } 
        catch (IOException e) {
            throw new RuntimeException("Failed to load workouts", e);
        }
    }
    
    /**
     * Get a workout by ID.
     * @param id the workout ID
     * @return Optional containing the workout if found
     */
    public Optional<Workout> getWorkoutById(Long id) {
        return getAllWorkouts().stream()
                .filter(workout -> id.equals(workout.getId()))
                .findFirst();
    }
    
    /**
     * Create a new workout.
     * @param workout the workout to create
     * @return the created workout with assigned ID
     */
    public Workout createWorkout(Workout workout) {
        // Assign ID to the workout
        workout.setId(nextId.getAndIncrement());
        
        try {
            fileManager.addWorkout(workout);
            return workout;
        } 
        catch (IOException e) {
            throw new RuntimeException("Failed to create workout", e);
        }
    }
    
    /**
     * Update an existing workout.
     * @param id the workout ID
     * @param updatedWorkout the updated workout data
     * @return the updated workout
     */
    public Workout updateWorkout(Long id, Workout updatedWorkout) {
        List<Workout> workouts = getAllWorkouts();
        
        // Find and update the workout
        for (int i = 0; i < workouts.size(); i++) {
            if (id.equals(workouts.get(i).getId())) {
                updatedWorkout.setId(id); // Ensure ID is preserved
                workouts.set(i, updatedWorkout);
                
                try {
                    fileManager.saveWorkouts(workouts);
                    return updatedWorkout;
                } 
                catch (IOException e) {
                    throw new RuntimeException("Failed to update workout", e);
                }
            }
        }
        
        throw new RuntimeException("Workout with ID " + id + " not found");
    }
    
    /**
     * Delete a workout by ID.
     * @param id the workout ID
     * @return true if workout was deleted, false if not found
     */
    public boolean deleteWorkout(Long id) {
        List<Workout> workouts = getAllWorkouts();
        
        boolean removed = workouts.removeIf(workout -> id.equals(workout.getId()));
        
        if (removed) {
            try {
                fileManager.saveWorkouts(workouts);
                return true;
            } 
            catch (IOException e) {
                throw new RuntimeException("Failed to delete workout", e);
            }
        }
        
        return false;
    }
}
