package core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single exercise with validated fields for name, repetitions, and weight.
 * All mutators enforce input validation so instances maintain a consistent, valid state.
 *
 * <p>Jackson annotations are used to support JSON (de)serialization.</p>
 */

public class Exercise {
    private String name;
    private int repetitions;
    private double weight;

    /**
     * Creates a new {@code Exercise} with the specified parameters.
     * All parameters are validated to ensure data integrity.
     *
     * @param name        the exercise name (e.g., "Bench Press"); must not be {@code null} or blank
     * @param repetitions the number of repetitions; must be {@code >= 0}
     * @param weight      the weight used per repetition (e.g., in kilograms); must be {@code >= 0}
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank,
     *                                  if {@code repetitions} is negative,
     *                                  or if {@code weight} is negative
     */
    @JsonCreator
    public Exercise(@JsonProperty("name") String name, 
                    @JsonProperty("repetitions") int repetitions, 
                    @JsonProperty("weight") double weight) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (repetitions < 0) {
            throw new IllegalArgumentException("Repetitions must be greater tham or equal to 0");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.name = name;
        this.repetitions = repetitions;
        this.weight = weight;
    }

    /** Returns the exercise name. */
    public String getName() {
        return name;
    }

    /**
     * Sets the exercise name.
     *
     * @param name the new name; must not be {@code null} or blank
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

     /** Returns the number of repetitions. */
    public int getRepetitions() {
        return repetitions;
    }

    /**
     * Sets the number of repetitions.
     *
     * @param repetitions the new number of repetitions; must be {@code >= 0}
     * @throws IllegalArgumentException if {@code repetitions} is negative
     */
    public void setRepetitions(int repetitions) {
        if (repetitions < 0) {
            throw new IllegalArgumentException("Repetitions must be greater than or equal to 0");
        }
        this.repetitions = repetitions;
    }

    /** Returns the weight used per repetition. */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight used per repetition.
     *
     * @param weight the new weight; must be {@code >= 0}
     * @throws IllegalArgumentException if {@code weight} is negative
     */
    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.weight = weight;
    }

    /** Returns a concise string representation of this exercise. */
    @Override
    public String toString() {
        return "Exercise{name='" + name + "', repetitions=" + repetitions + ", weight=" + weight + "}";
    }

}
