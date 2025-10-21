module workout.tracker.persistence {
    requires workout.tracker.core;
    requires java.base;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports persistence;
}
