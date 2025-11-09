/**
 * Core domain model and collections for the Workout Tracker.
 * Provides the {@code core} package with validated entities and utilities.
 */
module workout.tracker.core {
    requires java.base;
    requires javafx.base;

    requires com.fasterxml.jackson.annotation;

    // HTTP client for REST API communication
    requires java.net.http;

    // Jackson for JSON serialization/deserialization in REST client
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports core;
    exports core.service;
    opens core to com.fasterxml.jackson.databind;
}
