package ui;

/**
 * Centralized configuration constants for UI-level validation and limits.
 * 
 * Having these values in one place makes it easier to maintain and tweak
 * the application's constraints without changing logic in multiple classes.
 */
public final class uiConstants {

    private uiConstants() {}
    
    // --- Workout constraints ---
    public static final int MAX_EXERCISES_PER_WORKOUT = 5;
    public static final int MIN_NOTE_LENGTH = 3;
    public static final int MAX_NOTE_LENGTH = 300; // optional
    public static final int MAX_HOURS = 10;
    public static final int MINUTES_STEP = 5;

    // --- Exercise constraints ---
    public static final int MAX_REPS = 20;
    public static final double MAX_WEIGHT = 500.0;
    public static final double WEIGHT_STEP = 2.5;
}
