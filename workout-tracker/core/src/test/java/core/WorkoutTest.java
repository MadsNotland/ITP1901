package core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Workout validation and basic behavior.
 *
 * Focus:
 * - Constructor validation (invalid inputs should throw exceptions)
 * - Happy path (valid inputs set fields correctly)
 * - Exercises collection behavior (defensive copy, add/remove validation)
 */
class WorkoutTest {

    /**
     * Helper method to create an Exercise with a given name, sets and reps for reuse across tests.
     * Pass null for id to trigger auto-UUID generation.
     */
    private static Exercise exercise(String name, int reps, double weight) {
        return new Exercise(name, reps, weight,null);
    }

    /**
     * Helper method to create a sample list of Exercises for reuse across tests.
     */
    private static List<Exercise> sampleExercises() {
        return List.of(
                exercise("Bench press", 8, 60.0)
        );
    }

    /**
     * Helper method to create a valid Workout for reuse across tests.
     */
    private static Workout validWorkout() {
        return new Workout("Push", "Morning workout", LocalDate.parse("2025-09-15"), 1.5, sampleExercises());
    }

    /**
     * Tests that the constructor sets all fields correctly when given valid input.
     */
    @Test
    void constructor_setsAllFields_onValidInput() {
        LocalDate date = LocalDate.parse("2025-09-15");
        Workout w = new Workout("Push", "Morning workout", date, 1.5, sampleExercises());

        assertEquals("Push", w.getType());
        assertEquals("Morning workout", w.getNotes());
        assertEquals(date, w.getDate());
        assertEquals(1.5, w.getTime());
        assertEquals(1, w.getExercises().size());
        assertEquals("Bench press", w.getExercises().get(0).getName());
    }

    /**
     * Tests that invalid type values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenTypeIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(null, "ok", LocalDate.parse("2025-09-15"), 1.0, sampleExercises()));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("   ", "ok", LocalDate.parse("2025-09-15"), 1.0, sampleExercises()));
    }

    /**
     * Tests that invalid notes values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenNotesIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", null, LocalDate.parse("2025-09-15"), 1.0, sampleExercises()));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "\t\n ", LocalDate.parse("2025-09-15"), 1.0, sampleExercises()));
    }

    /**
     * Tests that a null date is rejected by the constructor.
     */
    @Test
    void constructor_throws_whenDateIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", null, 1.0, sampleExercises()));
    }

    /**
     * Tests that non-positive time values (0 or negative) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenTimeIsNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), 0.0, sampleExercises()));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), -1.0, sampleExercises()));
    }

    /**
     * Tests that a null or empty exercises list is rejected by the constructor.
     */
    @Test
    void constructor_throws_whenExercisesNullOrEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), 1.0, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), 1.0, List.of()));
    }

    /**
     * Tests that setters update fields correctly with valid values.
     */
    @Test
    void setters_updateFields_onValidInput() {
        Workout w = validWorkout();

        w.setId(1L);
        w.setType("Gym");
        w.setNotes("Strength session");
        w.setDate(LocalDate.parse("2025-09-16"));
        w.setTime(2.0);

        assertEquals(1L, w.getId());
        assertEquals("Gym", w.getType());
        assertEquals("Strength session", w.getNotes());
        assertEquals(LocalDate.parse("2025-09-16"), w.getDate());
        assertEquals(2.0, w.getTime());
    }

    /**
     * Tests that adding and removing exercises works as expected.
     */
    @Test
    void addExercise_andRemoveExercise_workAsExpected() {
        Workout w = validWorkout();
        Exercise squat = exercise("Squat", 5, 80.0);

        assertNull(w.findExerciseById(null));
        assertNull(w.findExerciseById(" "));
        assertNull(w.findExerciseById("\n\t "));

        w.addExercise(squat);
        assertEquals(2, w.getExercises().size());
        assertSame(squat, w.findExerciseById(squat.getId()));
        assertThrows(IllegalArgumentException.class, () -> w.addExercise(squat));

        w.removeExercise(squat);
        assertEquals(1, w.getExercises().size());
        assertNull(w.findExerciseById(squat.getId()));

        assertThrows(IllegalArgumentException.class, () -> w.removeExercise(squat));
    }

    /**
     * Tests that setters validate inputs and throw on invalid values.
     */
    @Test
    void setters_throw_onInvalidInput() {
        Workout w = validWorkout();

        assertThrows(IllegalArgumentException.class, () -> w.setType(null));
        assertThrows(IllegalArgumentException.class, () -> w.setType("   "));
        assertThrows(IllegalArgumentException.class, () -> w.setNotes(null));
        assertThrows(IllegalArgumentException.class, () -> w.setNotes("\n\t "));
        assertThrows(IllegalArgumentException.class, () -> w.setDate(null));
        assertThrows(IllegalArgumentException.class, () -> w.setTime(0.0));
        assertThrows(IllegalArgumentException.class, () -> w.setTime(-1.0));
        assertThrows(IllegalArgumentException.class, () -> w.addExercise(null));
        assertThrows(IllegalArgumentException.class, () -> w.removeExercise(null));
    }

    /**
     * Tests that toString contains the main fields for basic debugging visibility.
     */
    @Test
    void toString_containsAllFields() {
        Workout w = new Workout("Swim", "Pool", LocalDate.parse("2025-09-17"), 1.25, sampleExercises());
        String s = w.toString();
        assertAll(
                () -> assertTrue(s.contains("id=")),
                () -> assertTrue(s.contains("Swim")),
                () -> assertTrue(s.contains("Pool")),
                () -> assertTrue(s.contains("2025-09-17")),
                () -> assertTrue(s.contains("1.25")),
                () -> assertTrue(s.contains("Bench press"))
        );
    }
}
