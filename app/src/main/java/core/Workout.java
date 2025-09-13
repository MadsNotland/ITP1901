package core;

import java.time.LocalDate;

public class Workout {
    private LocalDate date;
    private double time;
    private String notes;
    private String type;

    public Workout(String type, String notes, LocalDate date, double time) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if(date == null){
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
         if (time <= 0) {
            throw new IllegalArgumentException("Time has to be bigger than 0");
        }
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
         if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
         if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "type='" + type + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
