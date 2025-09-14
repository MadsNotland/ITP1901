package core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WorkoutTracker functionality.
 * Tests workout addition, ordering, and defensive copying behavior.
 */
class WorkoutTrackerTest {

    /**
     * Helper method to create workout objects for testing.
     * 
     * @param type the workout type
     * @param notes the workout notes
     * @param date the workout date as a string (YYYY-MM-DD format)
     * @param time the workout duration in minutes
     * @return a new Workout object
     */
    private static Workout w(String type, String notes, String date, double time) {
        return new Workout(type, notes, LocalDate.parse(date), time);
    }
    
    /**
     * Tests that workouts are added at the front of the list (most recent first)
     * and that getWorkouts() returns a defensive copy of the internal list.
     */
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

        // Test defensive copying: getWorkouts() should return a copy, not the live list
        List<Workout> copy = tracker.getWorkouts();
        assertNotSame(tracker.getAllWorkouts(), copy);
        copy.clear();
        assertEquals(2, tracker.getAllWorkouts().size(), "Internal list should be unchanged");
    }
}
