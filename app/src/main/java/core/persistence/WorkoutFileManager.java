package core.persistence;

import core.Workout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class WorkoutFileManager {

    private final Path filePath;

    public WorkoutFileManager(Path filePath) {
        this.filePath = filePath;
    }

    //legger til alle workouts til fil (overskriver eksisterende innhold)
    public void saveWorkouts(List<Workout> workouts) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
        for (Workout ws : workouts) {
            writer.write(toCsvLine(ws));
            writer.newLine();
        }
    }   

    //legger til EN workout nederst i filen (lager fil hvis den ikke finnes)
    public void addWorkout(Workout ws) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(toCsvLine(ws));
            writer.newLine();
        }
    }

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


    //hjelpemetoder
    private String toCsvLine(Workout w) {
        return w.getType() + ";" + w.getNotes() + ";" + w.getDate() + ";" + w.getTime();
    }

    private Workout fromCsvLine(String line) {
        String[] parts = line.split(";", 4);
        String type = parts[0];
        String notes = parts[1];
        LocalDate date = LocalDate.parse(parts[2]);
        double time = Double.parseDouble(parts[3]);
        return new Workout(type, notes, date, time);
    }
}