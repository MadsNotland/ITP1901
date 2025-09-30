package persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import core.Workout;

/**
 * Manages persistence of workout data to and from CSV files.
 * Provides functionality to save, load, and append workout data.
 */
public class WorkoutFileManager {

    private final Path filePath;

    public WorkoutFileManager(Path filePath) {
        if (filePath == null) {
            this.filePath = Path.of("workout-tracker/data/workouts.csv");
        } else {
            this.filePath = filePath;
        }
    }

    /**
     * Saves all workouts to the CSV file, overwriting any existing content.
     *
     * @param workouts the list of workouts to save
     * @throws IOException if an I/O error occurs while writing to the file
     * @throws IllegalArgumentException if workouts list is null
     */
    public void saveWorkouts(List<Workout> workouts) throws IOException {
        if (workouts == null) {
            throw new IllegalArgumentException("Workouts cannot be null");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Workout ws : workouts) {
                writer.write(toCsvLine(ws));
                writer.newLine();
            }
        }
    }

    /**
     * Appends a single workout to the end of the CSV file.
     * Creates the file if it doesn't exist.
     *
     * @param workout the workout to append to the file
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void addWorkout(Workout workout) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(toCsvLine(workout));
            writer.newLine();
        }
    }

    /**
     * Loads all workouts from the CSV file.
     * Returns an empty list if the file doesn't exist.
     *
     * @return a list of all workouts from the file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<Workout> loadWorkouts() throws IOException {
        List<Workout> result = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return result;
        }

        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (!line.isBlank()) {
                result.add(fromCsvLine(line));
            }
        }
        return result;
    }


    /**
     * Converts a workout object to a CSV line format.
     *
     * @param workout the workout to convert
     * @return a CSV-formatted string representation of the workout
     */
    private String toCsvLine(Workout workout) {
        return workout.getType() + ";" + workout.getNotes() + ";" + workout.getDate() + ";" + workout.getTime();
    }

    /**
     * Converts a CSV line to a workout object.
     *
     * @param line the CSV line to parse
     * @return a workout object created from the CSV data
     * @throws IllegalArgumentException if the CSV line has invalid format
     */
    private Workout fromCsvLine(String line) {
        String[] parts = line.split(";", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid CSV format: " + line);
        }
        String type = parts[0];
        String notes = parts[1];
        LocalDate date = LocalDate.parse(parts[2]);
        double time = Double.parseDouble(parts[3]);
        return new Workout(type, notes, date, time);
    }
}
