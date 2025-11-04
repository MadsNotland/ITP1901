package server;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import core.Workout;

/**
 * REST controller for workout CRUD operations.
 */
@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*") // allows all clients
public class WorkoutController {

    private final WorkoutService workoutService;
    
    /**
     * Creates a new WorkoutController.
     *
     * @param workoutService the workout service
     */
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    /**
     * Retrieves all workouts from the system.
     *
     * @return ResponseEntity containing a list of all workouts with HTTP 200 status
     */
    @GetMapping({"", "/"})
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getAllWorkouts();
        return ResponseEntity.ok(workouts);
    }

    /**
     * Creates a new workout in the system.
     *
     * @param workout the workout data to create, provided in the request body
     * @return ResponseEntity containing the created workout with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        Workout createdWorkout = workoutService.createWorkout(workout);
        return ResponseEntity.status(201).body(createdWorkout);
    }

    /**
     * Retrieves a specific workout by its unique identifier.
     *
     * @param id the unique identifier of the workout to retrieve
     * @return ResponseEntity containing the workout with HTTP 200 status if found,
     *         or HTTP 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkout(@PathVariable("id") Long id) {
        Optional<Workout> workout = workoutService.getWorkoutById(id);
        return workout.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing workout with new data.
     *
     * @param id the unique identifier of the workout to update
     * @param workout the updated workout data, provided in the request body
     * @return ResponseEntity containing the updated workout with HTTP 200 status if successful,
     *         or HTTP 404 if the workout is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable("id") Long id, @RequestBody Workout workout) {
        try {
            Workout updatedWorkout = workoutService.updateWorkout(id, workout);
            return ResponseEntity.ok(updatedWorkout);
        } 
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a workout from the system.
     *
     * @param id the unique identifier of the workout to delete
     * @return ResponseEntity with HTTP 204 status if the workout was deleted,
     *         or HTTP 404 if the workout was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable("id") Long id) {
        boolean deleted = workoutService.deleteWorkout(id);
        return deleted ? ResponseEntity.noContent().build() 
                       : ResponseEntity.notFound().build();
    }
}
