package server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*") // tillater alle klienter
public class WorkoutController {

    @GetMapping
    public String getAllWorkouts() {
        return "World, Hello! alle workouts";
    }

    @PostMapping
    public ResponseEntity<String> createWorkout(@RequestBody String workoutJson) {
        // Returns 201 Created med melding
        return ResponseEntity.status(201).body("Workout created: " + workoutJson);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getWorkout(@PathVariable Long id) {
        // Returns 200 OK eller 404 Not Found
        return ResponseEntity.ok("Workout with ID: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateWorkout(@PathVariable Long id, @RequestBody String workoutJson) {
        // Returns 200 OK eller 404 Not Found
        return ResponseEntity.ok("Workout updated: " + id + " with data: " + workoutJson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkout(@PathVariable Long id) {
        // Returns 204 No Content eller 404 Not Found
        return ResponseEntity.noContent().build();
    }
}
