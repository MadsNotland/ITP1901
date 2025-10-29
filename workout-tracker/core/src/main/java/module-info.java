/**
 * Core domain model and collections for the Workout Tracker.
 * Provides the {@code core} package with validated entities and utilities.
 */
module workout.tracker.core {
    requires java.base;
    requires javafx.base;

    requires com.fasterxml.jackson.annotation;

    exports core;
    opens core to com.fasterxml.jackson.databind;
}
