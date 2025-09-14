package core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutTrackerTest {

    private static Workout w(String type, String notes, String date, double time) {
        return new Workout(type, notes, LocalDate.parse(date), time);
    }
    
    @Test
    void addsAtFrontAndGetWorkoutsReturnsCopy() {
        WorkoutTracker tracker = new WorkoutTracker();

        Workout w1 = w("Run",  "Easy run",  "2025-09-14", 30);
        Workout w2 = w("Gym", "Heavy","2025-09-15", 45);

        tracker.addWorkout(w1);
        tracker.addWorkout(w2); // should be inserted at index 0

        assertEquals(2, tracker.getAllWorkouts().size());
        assertSame(w2, tracker.getAllWorkouts().get(0));
        assertSame(w1, tracker.getAllWorkouts().get(1));

        // getWorkouts() should return a copy, not the live list
        List<Workout> copy = tracker.getWorkouts();
        assertNotSame(tracker.getAllWorkouts(), copy);
        copy.clear();
        assertEquals(2, tracker.getAllWorkouts().size(), "Internal list should be unchanged");
    }
}
