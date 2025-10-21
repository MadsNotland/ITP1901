package server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkoutController {

    @GetMapping("/api/workouts")
    public String getAllWorkouts() {
        return "World, Hello!";
    }
}
