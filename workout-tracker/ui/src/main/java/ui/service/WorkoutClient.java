package ui.service;

import java.io.IOException;
import java.util.List;

import core.Workout;

public interface WorkoutClient {
    List<Workout> getAllWorkouts() throws IOException;
    Workout createWorkout(Workout workout) throws IOException;
    Workout updateWorkout(Long id, Workout workout) throws IOException;
    void deleteWorkout(Long id) throws IOException;
}
