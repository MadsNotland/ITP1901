package server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import core.Workout;
import java.util.List;


@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*") // tillater alle klienter
public class WorkoutController {

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        // Returns empty list for now
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        return ResponseEntity.status(201).body(workout);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkout(@PathVariable Long id) {
        // For now return null - we will implement repository later
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable Long id, @RequestBody Workout workout) {
        // For now return workout - we will implement repository later
        return ResponseEntity.ok(workout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        // For now return 204 No Content - we will implement repository later
        return ResponseEntity.noContent().build();
    }   
}
