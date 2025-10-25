package core;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a workout session with validated fields for type, notes, date, and duration.
 * All mutators enforce input validation so instances maintain a consistent, valid state.
 */
public class Workout {
    private Long id;
    private LocalDate date;
    private double time;
    private String notes;
    private String type;

    /**
     * Creates a new workout with the specified parameters.
     * All parameters are validated to ensure data integrity.
     * 
     * @param type the type of workout (e.g., "Running", "Gym")
     * @param notes additional notes about the workout
     * @param date the date when the workout occurred
     * @param time the duration of the workout in hours
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @JsonCreator
    public Workout(@JsonProperty("type") String type, 
                   @JsonProperty("notes") String notes, 
                   @JsonProperty("date") LocalDate date, 
                   @JsonProperty("time") double time) {
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
        this.type = type;
        this.notes = notes;
        this.date = date;
        this.time = time;
    }

    /** Returns the workout ID. */
    public Long getId() {
        return id;
    }

    /** Sets the workout ID. */
    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
