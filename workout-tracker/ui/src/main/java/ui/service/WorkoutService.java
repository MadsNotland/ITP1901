package ui.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import core.Workout;
import core.WorkoutTracker;
import persistence.WorkoutFileManager;

/**
 * Service class responsible for managing persistence and data flow
 * for {@link Workout} and {@link WorkoutTracker}.
 *
 * <p>This class abstracts away file path resolution, loading, and saving
 * of workout data. It allows controllers (e.g. {@code MainController})
 * to interact with the business logic without worrying about file I/O.</p>
 *
 * <p>Design principles:
 * <ul>
 *   <li><b>Single Responsibility:</b> Handles only data operations.</li>
 *   <li><b>Separation of Concerns:</b> Keeps controllers UI-focused.</li>
 *   <li><b>Extensibility:</b> Can later be replaced with a REST API or database layer.</li>
 * </ul>
 * </p>
 */
public class WorkoutService {
    
    private final WorkoutFileManager fileManager;
    private final WorkoutTracker workoutTracker;

    // Sequence for assigning new Long ids
    private final AtomicLong idSequence = new AtomicLong(1L);

    /**
     * Creates a {@code WorkoutService} instance that initializes
     * a file manager with a resolved data path.
     */
    public WorkoutService() {
        Path workoutFilePath = resolveWorkoutFilePath(); 
        this.fileManager = new WorkoutFileManager(workoutFilePath);
        this.workoutTracker = new WorkoutTracker();
    }

    /**
     * Loads all workouts from the configured data file.
     *
     * @throws IOException if file reading or parsing fails
     */
    public void loadWorkouts() throws IOException {
        var loadedWorkouts = fileManager.loadWorkouts();

        // initialize the sequence to max(existingId)+1
        long next = loadedWorkouts.stream()
                .map(Workout::getId)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;
        idSequence.set(next);

        workoutTracker.getAllWorkouts().setAll(loadedWorkouts);
    }

    /**
     * Saves all workouts currently in the {@link WorkoutTracker} to file.
     *
     * @throws IOException if file writing fails
     */
    public void saveWorkouts() throws IOException {
        List<Workout> workouts = List.copyOf(workoutTracker.getAllWorkouts());
        fileManager.saveWorkouts(workouts);
    }

    /**
     * Returns the shared {@link WorkoutTracker} instance used by the application.
     *
     * @return the internal {@code WorkoutTracker}
     */
    public WorkoutTracker getWorkoutTracker() {
        return workoutTracker;
    }

    public void addWorkout(Workout workout) {
        // Assign an id if missing
        if (workout.getId() == null) {
            workout.setId(idSequence.getAndIncrement());
        }
        workoutTracker.addWorkout(workout);
    }

    /**
     * Resolves the most likely file path for the workouts.json file
     * across different runtime environments (IDE, Maven, packaged app).
     *
     * @return path to the workouts.json data file
     */
    private Path resolveWorkoutFilePath() {
        String currentDir = System.getProperty("user.dir");
        if (currentDir == null) {
            throw new IllegalStateException("Cannot determine working directory");
        }

        Path cwd = Paths.get(currentDir).toAbsolutePath();
        // Primary: running from the UI module directory: workout-tracker/ui -> ../data/workouts.json
        Path cwdParent = cwd.getParent();
        Path candidate = cwdParent != null
                ? cwdParent.resolve("data").resolve("workouts.json")
                : cwd.resolve("data").resolve("workouts.json");
        
        // Fallback: running from project root: workout-tracker -> data/workouts.json
        if (!Files.exists(candidate)) {
            Path projectRootCandidate = cwd.resolve("data").resolve("workouts.json");
            if (Files.exists(projectRootCandidate)) {
                candidate = projectRootCandidate;
            }
        }

        // Last resort: legacy repository layout if present
        if (!Files.exists(candidate)) {
            Path legacy = cwd.resolve("app").resolve("data").resolve("workouts.json");
            if (Files.exists(legacy)) {
                candidate = legacy;
            }
        }
        return candidate;
    }
}
