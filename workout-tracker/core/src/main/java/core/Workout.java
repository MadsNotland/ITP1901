package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a workout session with validated fields for type, notes, date, duration and exercises.
 * All mutators enforce input validation so instances maintain a consistent, valid state.
 */
public class Workout {
    private LocalDate date;
    private double time;
    private String notes;
    private String type;
    private final List<Exercise> exercises = new ArrayList<>();

    /**
     * Creates a new workout with the specified parameters.
     * All parameters are validated to ensure data integrity.
     * 
     * @param type the type of workout (e.g., "Running", "Gym")
     * @param notes additional notes about the workout
     * @param date the date when the workout occurred
     * @param time the duration of the workout in hours
     * @param exercises the list of exercises performed during the workout
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @JsonCreator
    public Workout(@JsonProperty("type") String type, 
                   @JsonProperty("notes") String notes, 
                   @JsonProperty("date") LocalDate date, 
                   @JsonProperty("time") double time,
                   @JsonProperty("exercises") List<Exercise> exercises) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        if (time <= 0) {
            throw new IllegalArgumentException("Time has to be bigger than 0");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        if (exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("Exercises cannot be null or empty");
        }
        this.type = type;
        this.notes = notes;
        this.date = date;
        this.time = time;
        this.exercises.addAll(exercises);
    }

    /** Returns the workout date. */
    public LocalDate getDate() {
        return date;
    }

    /** Sets the workout date; rejects {@code null}. */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    /** Returns the workout duration in hours. */
    public double getTime() {
        return time;
    }

    /** Sets the workout duration in hours; must be strictly positive. */
    public void setTime(double time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time has to be bigger than 0");
        }
        this.time = time;
    }

    /** Returns the notes. */
    public String getNotes() {
        return notes;
    }

    /** Sets the notes; rejects {@code null} or blank. */
    public void setNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        this.notes = notes;
    }

    /** Returns the workout type. */
    public String getType() {
        return type;
    }

    /** Sets the workout type; rejects {@code null} or blank. */
    public void setType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        this.type = type;
    }

    /** Returns the list of exercises performed during the workout. */
    public List<Exercise> getExercises() {
        return new ArrayList<>(exercises);
    }

    /** Finds and returns an exercise by its id; returns {@code null} if not found. */
    public Exercise findExerciseById(String id) {
        if (id == null || id.isBlank()) return null;
        for (Exercise e : exercises) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    /** Adds an exercise to the workout; rejects {@code null}. */
    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        this.exercises.add(exercise);
    }

    /** Removes an exercise from the workout; rejects {@code null} or non-existent exercises. */
    public void removeExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        Exercise toRemove = findExerciseById(exercise.getId());
        if (toRemove == null) {
            throw new IllegalArgumentException("Exercise not found in workout");
        }
        exercises.remove(toRemove);
    }

    @Override
    public String toString() {
        return "Workout{" +
                "type='" + type + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", exercises=" + exercises +
                '}';
    }
}
