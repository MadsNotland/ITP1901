package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.Workout;

/**
 * Manages persistence of workout data to and from JSON files using Jackson.
 * Provides functionality to save, load, and append workout data.
 * 
 * <p>This class uses Jackson ObjectMapper for JSON serialization and deserialization,
 * with JavaTimeModule support for LocalDate handling. The JSON format stores workouts
 * as an arrat of objects with type, notes, date and time.
 * 
 * <p> All file operations are performed atomically.
 */
public class WorkoutFileManager {

    private final Path filePath;
    private final ObjectMapper objectMapper;

    // Creates a new WorkoutFileManager with the spesified file path.
    public WorkoutFileManager(Path filePath) {
        if (filePath == null) {
            this.filePath = Path.of("workout-tracker/data/workouts.json");
        } else {
            this.filePath = filePath;
        }
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Saves all workouts to the JSON file, overwriting any existing content.
     * 
     * <p> Uses Jackson's pretty printer for human-readable JSON output.
     *
     * @param workouts the list of workouts to save
     * @throws IOException if an I/O error occurs while writing to the file
     * @throws IllegalArgumentException if workouts list is null
     */
    public void saveWorkouts(List<Workout> workouts) throws IOException {
        if (workouts == null) {
            throw new IllegalArgumentException("Workouts cannot be null");
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), workouts);
    }

    /**
     * Appends a single workout to the end of the JSON file.
     * Creates the file if it doesn't exist.
     *
     * @param workout the workout to append to the file
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void addWorkout(Workout workout) throws IOException {
        List<Workout> existingWorkouts = loadWorkouts();
        existingWorkouts.add(workout);
        saveWorkouts(existingWorkouts);
    }

    /**
     * Loads all workouts from the JSON file.
     * Returns an empty list if the file doesn't exist.
     *
     * @return a list of all workouts from the file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<Workout> loadWorkouts() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(filePath.toFile(),
               objectMapper.getTypeFactory().constructCollectionType(List.class, Workout.class));
    }
}
