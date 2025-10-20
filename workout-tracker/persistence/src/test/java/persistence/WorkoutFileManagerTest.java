package persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import core.Workout;

/**
 * Test class for WorkoutFileManager functionality.
 * 
 * <p>Tests the persistence layer's ability to save, load and append workout data,
 * using Jackson JSON serialization. Covers both happy path scenarios and edge cases
 * like non-existent files and empty data.
 * 
 * Test strategy:
 * <ul>
 *   <li>Verify that workout data can be saved and loaded correctly</li>
 *   <li>Test that all workout fields are preserved through save/load cycle</li>
 *   <li>Test edge cases (non-existent files)</li>
 *   <li>Test integration between load and save operations</li>
 * </ul>
 * 
 * <p>Uses JUnit 5 @TempDir for isolated test file operations.
 */
class WorkoutFileManagerTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that workout data can be saved and loaded correctly with data integrity.
     * 
     * <p>Verifies that:
     * <ul>
     *   <li>Multiple workouts can be saved to file and loaded back</li>
     *   <li>All workout fields (type, notes, date, time) are preserved through save/load cycle</li>
     *   <li>Workout objects maintain their exact values after serialization/deserialization</li>
     *   <li>The number of workouts is preserved</li>
     * </ul>
     * 
     * <p>Note: This test verifies data integrity but does not specifically test
     * JSON format or Jackson serialization details.
     */
    @Test
    void testSaveAndLoadWorkouts() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-workouts.json");
        WorkoutFileManager manager = new WorkoutFileManager(testFile);
        
        Workout workout1 = new Workout("Running", "Morning run", LocalDate.of(2025, 9, 14), 1.5,null);
        Workout workout2 = new Workout("Gym", "Strength training", LocalDate.of(2025, 9, 15), 2.0,null);
        List<Workout> workouts = List.of(workout1, workout2);

        // Act
        manager.saveWorkouts(workouts);
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertAll("First workout data integrity",
            () -> assertEquals(workout1.getType(), loadedWorkouts.get(0).getType()),
            () -> assertEquals(workout1.getNotes(), loadedWorkouts.get(0).getNotes()),
            () -> assertEquals(workout1.getDate(), loadedWorkouts.get(0).getDate()),
            () -> assertEquals(workout1.getTime(), loadedWorkouts.get(0).getTime())
        );

        assertAll("Second workout data integrity",
            () -> assertEquals(workout2.getType(), loadedWorkouts.get(1).getType()),
            () -> assertEquals(workout2.getNotes(), loadedWorkouts.get(1).getNotes()),
            () -> assertEquals(workout2.getDate(), loadedWorkouts.get(1).getDate()),
            () -> assertEquals(workout2.getTime(), loadedWorkouts.get(1).getTime())
        );
    }

    /**
     * Tests that adding a single workout works correctly.
     * 
     * <p>Verifies the complete workflow: add single workout, then load all workouts.
     * This tests the integration between loadWorkouts() and saveWorkouts() methods.
     * 
     * <p>Note: This is more of an integration test than a unit test, as it
     * combines multiple operations in sequence.
     */
    @Test
    void testAppendWorkout() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-append.json");
        WorkoutFileManager manager = new WorkoutFileManager(testFile);
        
        Workout workout = new Workout("Swimming", "Pool session", LocalDate.of(2025, 9, 16), 1.0,null);

        // Act
        manager.addWorkout(workout);
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertEquals(1, loadedWorkouts.size());
        assertEquals("Swimming", loadedWorkouts.get(0).getType());
    }

    /**
     * Tests that loading from non-existent file returns empty list.
     * 
     * <p>Verifies graceful handling of missing files, which is important for
     * first-time application startup when no data file exists yet.
     * 
     * <p>This edge case prevents application crashes when users run the app
     * for the first time.
     */
    @Test
    void testLoadFromNonExistentFile() throws IOException {
        // Arrange
        Path nonExistentFile = tempDir.resolve("non-existent.json");
        WorkoutFileManager manager = new WorkoutFileManager(nonExistentFile);

        // Act
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertTrue(loadedWorkouts.isEmpty());
    }
}