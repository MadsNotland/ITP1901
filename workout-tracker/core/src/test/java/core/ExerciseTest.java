package core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        return new Exercise("Bench Press", 8, 60.0,null);
    }

    /**
     * Tests that the constructor sets all fields correctly when given valid input.
     */
    @Test
    void constructor_setsAllFields_onValidInput() {
        Exercise e = new Exercise("Squat", 5, 100.0,null);

        assertEquals("Squat", e.getName());
        assertEquals(5, e.getRepetitions());
        assertEquals(100.0, e.getWeight());
    }
    

    /**
     * Boundary: repetitions = 0 and weight = 0 should be accepted.
     */
    @Test
    void constructor_acceptsZeroForRepetitionsAndWeight() {
        Exercise e = new Exercise("Plank", 0, 0.0,null);

        assertEquals("Plank", e.getName());
        assertEquals(0, e.getRepetitions());
        assertEquals(0.0, e.getWeight());
    }

    /**
     * Tests that invalid name values (null/blank) are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenNameIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise(null, 1, 1.0,null));
        assertThrows(IllegalArgumentException.class, () -> new Exercise("   ", 1, 1.0,null));
    }

     /**
     * Tests that negative repetitions are rejected by the constructor.
     */
    @Test
    void constructor_throws_whenRepetitionsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise("Pushup", -1, 10.0,null));
    }

    /**
     * Tests that negative weight is rejected by the constructor.
     */
    @Test
    void constructor_throws_whenWeightNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Exercise("Deadlift", 3, -0.1,null));
    }
    /**
     * Tests that a random UUID is generated when id is null or blank.
     */
    @Test
    void constructor_generatesId_whenIdBlank() {
        Exercise e = new Exercise("Test", 1, 1.0, " ");
        assertNotNull(e.getId());
        assertFalse(e.getId().isBlank());
    }
    /**
     * Tests that the provided id is used when it is not blank.
     */
    @Test
    void constructor_usesProvidedId_whenNotBlank() {
        String id = "abc123";
        Exercise e = new Exercise("Test", 1, 1.0, id);
        assertEquals(id, e.getId());
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
        Exercise e = new Exercise("Row", 12, 55.5,null);
        String s = e.toString();
        assertAll(
                () -> assertTrue(s.contains("Row")),
                () -> assertTrue(s.contains("12")),
                () -> assertTrue(s.contains("55.5"))
        );
    }
    /**
     * Tests that equals and hashCode are based on the unique identifier (id) only.
     */
    @Test
    void equals_returnsTrue_forSameId() {
        Exercise a = new Exercise("A", 1, 1.0, "id1");
        Exercise b = new Exercise("B", 2, 2.0, "id1");
        Exercise c = new Exercise("C", 2, 2.0, "id1");
        //reflexive
        assertTrue(a.equals(a));
        //Symmetric
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        //Transitive
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
    }
    /**
     * Tests that equals returns false for different ids or null.
     */
    @Test
    void equals_returnsFalse_forDifferentIdOrNull() {
        Exercise a = new Exercise("A", 1, 1.0, "id1");
        Exercise b = new Exercise("A", 1, 1.0, "id2");
        assertNotEquals(a, b);
        assertNotEquals(a, null);
    }
    /**
     * Tests that hashCode is consistent with equals based on id.
     */
    @Test
    void hashCode_matchesForEqualObjects() {
        Exercise a = new Exercise("A", 1, 1.0, "id1");
        Exercise b = new Exercise("B", 2, 2.0, "id1");
        assertEquals(a.hashCode(), b.hashCode());
    }

}
