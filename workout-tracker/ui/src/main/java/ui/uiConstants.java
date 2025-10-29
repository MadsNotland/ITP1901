package ui;

/**
 * Centralized configuration constants for UI-level validation and limits.
 * 
 * Having these values in one place makes it easier to maintain and tweak
 * the application's constraints without changing logic in multiple classes.
 */
public final class UiConstants {

    private UiConstants() { }
    
    // --- Workout constraints ---
    /** Maximum number of exercises allowed per workout. */
    public static final int MAX_EXERCISES_PER_WORKOUT = 5;

    /** Minimum number of characters allowed in workout notes. */
    public static final int MIN_NOTE_LENGTH = 3;

    /** Maximum number of characters allowed in workout notes. */
    public static final int MAX_NOTE_LENGTH = 300; // optional

    /** Maximum hours allowed for a single workout. */
    public static final int MAX_HOURS = 10;

    /** Step size for selectable minutes. */
    public static final int MINUTES_STEP = 5;


    // --- Exercise constraints ---
    /** Maximum repetitions allowed per exercise. */
    public static final int MAX_REPS = 20;

    /** Maximum weight allowed per exercise (in kilograms). */
    public static final double MAX_WEIGHT = 500.0;

    /** Step size for selectable weights (in kilograms). */
    public static final double WEIGHT_STEP = 2.5;
}
