package core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Workout validation and basic behavior.
 *
 * Focus:
 * - Constructor validation (invalid inputs should throw exceptions)
 * - Happy path (valid inputs set fields correctly)
 */
class WorkoutTest {

    /**
     * Helper method to create a valid Workout for reuse across tests.
     */
    private static Workout validWorkout() {
        return new Workout("Run", "Morning jog", LocalDate.parse("2025-09-15"), 1.5);
    }

    /**
     * Tests that the constructor sets all fields correctly when given valid input.
     */
    @Test
    void constructor_setsAllFields_onValidInput() {
        LocalDate date = LocalDate.parse("2025-09-15");
        Workout w = new Workout("Run", "Morning jog", date, 1.5);

        assertEquals("Run", w.getType());
        assertEquals("Morning jog", w.getNotes());
        assertEquals(date, w.getDate());
        assertEquals(1.5, w.getTime());
    }

    /**
     * Tests that invalid type values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenTypeIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout(null, "ok", LocalDate.parse("2025-09-15"), 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("   ", "ok", LocalDate.parse("2025-09-15"), 1.0));
    }

    /**
     * Tests that invalid notes values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenNotesIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", null, LocalDate.parse("2025-09-15"), 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "\t\n ", LocalDate.parse("2025-09-15"), 1.0));
    }

    /**
     * Tests that a null date is rejected by the constructor.
     */
    @Test
    void constructor_throws_whenDateIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", null, 1.0));
    }

    /**
     * Tests that non-positive time values (0 or negative) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenTimeIsNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Workout("Run", "ok", LocalDate.parse("2025-09-15"), -1.0));
    }
}


