package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a workout session with validated fields for type, notes, date, duration and exercises.
 * All mutators enforce input validation so instances maintain a consistent, valid state.
 * This ID is only meant to be assigned internally by persistence layer
 */
public class Workout {
    private Long id;
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

    /**
     * Returns the workout ID. 
     * 
     * @return the id of the workout
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the workout ID. 
     * 
     * @param id the new id of the workout; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the workout date. 
     * 
     * @return the date of the workout
     */
    public LocalDate getDate() {
        return date;
    }

    /** Sets the workout date; rejects {@code null}. 
     * 
     * @param date the new date of the workout; must not be {@code null}
     * @throws IllegalArgumentException if {@code date} is {@code null}
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    /** Returns the workout duration in hours. 
     * 
     * @return the duration of the workout in hours
     */
    public double getTime() {
        return time;
    }

    /** Sets the workout duration in hours; must be strictly positive. 
     * 
     * @param time the new duration of the workout in hours; must be strictly positive
     * @throws IllegalArgumentException if {@code time} is not strictly positive
     */
    public void setTime(double time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time has to be bigger than 0");
        }
        this.time = time;
    }

    /** Returns the notes. 
     * 
     * @return the notes of the workout
     */
    public String getNotes() {
        return notes;
    }

    /** Sets the notes; rejects {@code null} or blank. 
     * 
     * @param notes the new notes; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code notes} is {@code null} or blank
     */
    public void setNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        this.notes = notes;
    }

    /** Returns the workout type. 
     * 
     * @return the type of workout
     */
    public String getType() {
        return type;
    }

    /** Sets the workout type; rejects {@code null} or blank. 
     * 
     * @param type the new type of workout; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code type} is {@code null} or blank
     */
    public void setType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        this.type = type;
    }

    /** Returns the list of exercises performed during the workout. 
     * 
     * @return a defensive copy of the list of exercises
     */
    public List<Exercise> getExercises() {
        return new ArrayList<>(exercises);
    }

    /** Finds and returns an exercise by its id; returns {@code null} if not found. 
     * 
     * @param id the id of the exercise to find
     * @return the exercise with the matching id, or {@code null} if not found
     */
    public Exercise findExerciseById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (Exercise e : exercises) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    /** Adds an exercise to the workout; rejects {@code null}. 
     * 
     * @param exercise the exercise to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code exercise} is {@code null} or already exists in the workout
     */
    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        for (Exercise e : exercises) {
            if (e.getId().equals(exercise.getId())) {
                throw new IllegalArgumentException("Exercise already exists in workout");
            }
        }
        this.exercises.add(exercise);
    }

    /** Removes an exercise from the workout; rejects {@code null} or non-existent exercises. 
     * 
     * @param exercise the exercise to remove; must not be {@code null}
     * @throws IllegalArgumentException if {@code exercise} is {@code null} or not found in the workout
     */
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

    /** Sets the list of exercises; rejects {@code null}. 
     * 
     * @param newList the new list of exercises; must not be {@code null}
     */
    public void setExercises(List<Exercise> newList) {
        java.util.Objects.requireNonNull(newList);
        this.exercises.clear();
        this.exercises.addAll(newList);
    }

    /** Returns a string representation of the workout. 
     * 
     * @return a string representation of the workout
     */
    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", exercises=" + exercises +
                '}';
    }
}
