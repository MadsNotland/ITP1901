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
     * @param type the type of workout
     * @param notes the notes of the workout
     * @param date the date of the workout
     * @param time the time of the workout
     * @param exercises the exercises of the workout
     * @return the created workout
     */
    private static Workout w(String type, String notes, String date, double time, List<Exercise> exercises) {
        return new Workout(type, notes, LocalDate.parse(date), time, exercises);
    }

    /**
     * Helper method to create an Exercise with a given name, sets and reps for reuse across tests.
     * Pass null for id to trigger auto-UUID generation.
     * 
     * @param name the name of the exercise
     * @param reps the number of repetitions
     * @param weight the weight of the exercise
     * @return the created exercise
     */
    private static Exercise exercise(String name, int reps, double weight) {
        return new Exercise(name, reps, weight,null);
    }

    /**
     * Helper method to create a sample list of Exercises for reuse across tests.
     * 
     * @return the created list of exercises
     */
    private static List<Exercise> pushExercises() {
        return List.of(
                exercise("Bench press", 8, 60.0)
        );
    }

    /**
     * Helper method to create a sample list of Exercises for reuse across tests.
     * 
     * @return the created list of exercises
     */
    private static List<Exercise> pullExercises() {
        return List.of(
                exercise("Pull ups", 8, 0.0)
        );
    }
    
    /**
     * Tests that workouts are added at the front of the list (most recent first)
     * and that getWorkouts() returns a defensive copy of the internal list.
     * 
     * @throws AssertionError if the workouts are not added at the front of the list
     * @throws AssertionError if the getWorkouts() method returns a live list
     */
    @Test
    void addsAtFrontAndGetWorkoutsReturnsCopy() {
        WorkoutTracker tracker = new WorkoutTracker();

        Workout w1 = w("Push",  "At home",  "2025-09-14", 30, pushExercises());
        Workout w2 = w("Pull", "Gym","2025-09-15", 45,pullExercises());

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
     * 
     * @throws IllegalArgumentException if the workout is null
     */
    @Test
    void addWorkout_throwsOnNull() {
        WorkoutTracker tracker = new WorkoutTracker();
        assertThrows(IllegalArgumentException.class, () -> tracker.addWorkout(null));
    }

    /**
     * Tests that getAllWorkouts returns a live list connected to the tracker.
     * Mutations on this list should reflect in the tracker state.
     * 
     * @throws AssertionError if the getAllWorkouts() method returns a live list
     */
    @Test
    void getAllWorkouts_isLiveList() {
        WorkoutTracker tracker = new WorkoutTracker();
        assertEquals(0, tracker.getAllWorkouts().size());

        // Add via the live list reference
        tracker.getAllWorkouts().add(new Workout("Bike", "Ride", LocalDate.parse("2025-09-16"), 1.0,
                List.of(exercise("Cycling", 0, 0.0))));
        assertEquals(1, tracker.getWorkouts().size());
        assertEquals("Bike", tracker.getWorkouts().get(0).getType());
    }
}
