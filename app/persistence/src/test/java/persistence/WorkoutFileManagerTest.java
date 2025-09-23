package persistence;

import core.Workout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WorkoutFileManager functionality.
 * Tests basic file operations: save, load, and append.
 */
class WorkoutFileManagerTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that workouts can be saved and loaded correctly.
     * This is the core functionality of the file manager.
     */
    @Test
    void testSaveAndLoadWorkouts() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-workouts.csv");
        WorkoutFileManager manager = new WorkoutFileManager(testFile);
        
        Workout workout1 = new Workout("Running", "Morning run", LocalDate.of(2025, 9, 14), 1.5);
        Workout workout2 = new Workout("Gym", "Strength training", LocalDate.of(2025, 9, 15), 2.0);
        List<Workout> workouts = List.of(workout1, workout2);

        // Act
        manager.saveWorkouts(workouts);
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertEquals(2, loadedWorkouts.size());
        assertEquals("Running", loadedWorkouts.get(0).getType());
        assertEquals("Gym", loadedWorkouts.get(1).getType());
    }

    /**
     * Tests that appending a workout works correctly.
     */
    @Test
    void testAppendWorkout() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-append.csv");
        WorkoutFileManager manager = new WorkoutFileManager(testFile);
        
        Workout workout = new Workout("Swimming", "Pool session", LocalDate.of(2025, 9, 16), 1.0);

        // Act
        manager.addWorkout(workout);
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertEquals(1, loadedWorkouts.size());
        assertEquals("Swimming", loadedWorkouts.get(0).getType());
    }

    /**
     * Tests that loading from non-existent file returns empty list.
     */
    @Test
    void testLoadFromNonExistentFile() throws IOException {
        // Arrange
        Path nonExistentFile = tempDir.resolve("non-existent.csv");
        WorkoutFileManager manager = new WorkoutFileManager(nonExistentFile);

        // Act
        List<Workout> loadedWorkouts = manager.loadWorkouts();

        // Assert
        assertTrue(loadedWorkouts.isEmpty());
    }
}