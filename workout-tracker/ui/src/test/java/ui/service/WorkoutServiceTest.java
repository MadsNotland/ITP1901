package ui.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import core.Exercise;
import core.Workout;

class WorkoutServiceTest {

    @Test
    void addWorkout_appendsToTrackerList() {
        WorkoutService service = new WorkoutService();
        int before = service.getWorkoutTracker().getAllWorkouts().size();

        Workout w = new Workout(
            "Run",
            "Easy jog",
            LocalDate.of(2025, 1, 2),
            0.5,
            List.of(new Exercise("Sample Exercise", 10, 20.0, null))
        );

        service.addWorkout(w);

        int after = service.getWorkoutTracker().getAllWorkouts().size();
        assertEquals(before + 1, after);
    }
}


