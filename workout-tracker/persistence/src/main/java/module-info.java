module workout.tracker.persistence {
    // Use 'transitive' so clients of this module can access core types from exported packages
    requires transitive workout.tracker.core;
    requires java.base;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports persistence;
}
