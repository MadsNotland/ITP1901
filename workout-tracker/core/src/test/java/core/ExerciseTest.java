package core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for Exercise validation and basic behavior.
 *
 * Focus:
 * - Constructor validation (invalid inputs should throw exceptions)
 * - Happy path (valid inputs set fields correctly)
 * - Boundary values (0 repetitions/weight are allowed)
 */
public class ExerciseTest {

    /**
     * Helper to create a valid Exercise reused across tests.
     */
    private static Exercise validExercise() {
        return new Exercise("Bench Press", 8, 60.0);
    }

    /**
     * Tests that the constructor sets all fields correctly when given valid input.
     */
    @Test
    void constructor_setsAllFields_onValidInput() {
        Exercise e = new Exercise("Squat", 5, 100.0);

        assertEquals("Squat", e.getName());
        assertEquals(5, e.getRepetitions());
        assertEquals(100.0, e.getWeight());
    }

    /**
     * Boundary: repetitions = 0 and weight = 0 should be accepted.
     */
    @Test
    void constructor_acceptsZeroForRepetitionsAndWeight() {
        Exercise e = new Exercise("Plank", 0, 0.0);

        assertEquals("Plank", e.getName());
        assertEquals(0, e.getRepetitions());
        assertEquals(0.0, e.getWeight());
    }

    /**
     * Tests that invalid name values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenNameIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise(null, 1, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Exercise("   ", 1, 1.0));
    }

     /**
     * Tests that negative repetitions are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenRepetitionsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise("Pushup", -1, 10.0));
    }

    /**
     * Tests that negative weight is rejected by the constructor.
     */
    @Test
    void constructor_throws_whenWeightNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise("Deadlift", 3, -0.1));
    }

    /**
     * Tests that setters update fields correctly with valid values.
     */
    @Test
    void setters_updateFields_onValidInput() {
        Exercise e = validExercise();

        e.setName("Overhead Press");
        e.setRepetitions(10);
        e.setWeight(42.5);

        assertEquals("Overhead Press", e.getName());
        assertEquals(10, e.getRepetitions());
        assertEquals(42.5, e.getWeight());
    }

    /**
     * Boundary: setters accept zero for repetitions and weight.
     */
    @Test
    void setters_acceptZeroValues() {
        Exercise e = validExercise();

        e.setRepetitions(0);
        e.setWeight(0.0);

        assertEquals(0, e.getRepetitions());
        assertEquals(0.0, e.getWeight());
    }

    /**
     * Tests that setters validate inputs and throw on invalid values.
     */
    @Test
    void setters_throw_onInvalidInput() {
        Exercise e = validExercise();

        assertThrows(IllegalArgumentException.class, () -> e.setName(null));
        assertThrows(IllegalArgumentException.class, () -> e.setName("\n\t "));
        assertThrows(IllegalArgumentException.class, () -> e.setRepetitions(-1));
        assertThrows(IllegalArgumentException.class, () -> e.setWeight(-0.01));
    }

    /**
     * Tests that toString contains the main fields for basic debugging visibility.
     */
    @Test
    void toString_containsAllFields() {
        Exercise e = new Exercise("Row", 12, 55.5);
        String s = e.toString();
        assertAll(
                () -> assertTrue(s.contains("Row")),
                () -> assertTrue(s.contains("12")),
                () -> assertTrue(s.contains("55.5"))
        );
    }
}
