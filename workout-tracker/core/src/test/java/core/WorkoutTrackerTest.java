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

    /**
     * Tests that adding a null workout is not allowed and throws an exception.
     */
    @Test
    void addWorkout_throwsOnNull() {
        WorkoutTracker tracker = new WorkoutTracker();
        assertThrows(IllegalArgumentException.class, () -> tracker.addWorkout(null));
    }

    /**
     * Tests that getAllWorkouts returns a live list connected to the tracker.
     * Mutations on this list should reflect in the tracker state.
     */
    @Test
    void getAllWorkouts_isLiveList() {
        WorkoutTracker tracker = new WorkoutTracker();
        assertEquals(0, tracker.getAllWorkouts().size());

        // Add via the live list reference
        tracker.getAllWorkouts().add(new Workout("Bike", "Ride", LocalDate.parse("2025-09-16"), 1.0));
        assertEquals(1, tracker.getWorkouts().size());
        assertEquals("Bike", tracker.getWorkouts().get(0).getType());
    }
}
