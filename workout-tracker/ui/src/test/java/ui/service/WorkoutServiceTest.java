package ui.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import core.Exercise;
import core.Workout;

/**
 * Test class for WorkoutService functionality.
 * 
 * <p>Tests that WorkoutService correctly interacts with WorkoutClient
 * and maintains workout data in the WorkoutTracker.</p>
 * 
 * <p>This test uses a mock implementation of RestWorkoutClient to avoid
 * making actual HTTP requests during unit testing. The mock simulates
 * server behavior by assigning IDs and returning created workouts.</p>
 */
class WorkoutServiceTest {

    /**
     * Tests that adding a workout via WorkoutService correctly:
     * 1. Calls the REST client to create the workout (server assigns ID)
     * 2. Adds the workout with server-assigned ID to the tracker
     * 3. Increases the tracker's workout count by one
     * 
     * <p>This test uses a mock RestWorkoutClient that simulates server
     * behavior without requiring an actual running server.</p>
     */
    @Test
    void addWorkout_appendsToTrackerList() throws IOException {
        // Arrange: Create a mock REST client that simulates server behavior
        MockWorkoutClient mockClient = new MockWorkoutClient();
        WorkoutService service = new WorkoutService(mockClient);
        int before = service.getWorkoutTracker().getAllWorkouts().size();

        // Create a workout without ID (as it would be created in UI)
        Workout workoutToAdd = new Workout(
            "Run",
            "Easy jog",
            LocalDate.of(2025, 1, 2),
            0.5,
            List.of(new Exercise("Sample Exercise", 10, 20.0, null))
        );

        // Act: Add workout through service (which calls mock REST client)
        service.addWorkout(workoutToAdd);

        // Assert: Tracker should contain one more workout with server-assigned ID
        int after = service.getWorkoutTracker().getAllWorkouts().size();
        assertEquals(before + 1, after, "Tracker should contain one more workout after adding");
        
        // Verify that the workout in tracker has an ID assigned by the mock server
        Workout addedWorkout = service.getWorkoutTracker().getAllWorkouts().get(before);
        assertEquals(1L, addedWorkout.getId(), "Workout should have server-assigned ID of 1");
    }

    /**
     * Mock implementation of WorkoutClient for unit testing.
     * 
     * <p>This mock simulates server behavior without making actual HTTP requests:
     * - Maintains an internal list of workouts
     * - Assigns sequential IDs to workouts (simulating server ID generation)
     * - Returns workouts with assigned IDs (simulating server response)</p>
     * 
     * <p>This allows WorkoutService to be tested in isolation without requiring
     * a running server or network connectivity.</p>
     */
    private static class MockWorkoutClient implements WorkoutClient {
        
        private final List<Workout> mockWorkouts = new ArrayList<>();
        private long nextId = 1L;

        /**
         * Creates a mock REST client that simulates server behavior.
         * 
         * <p>Note: This calls the parent constructor which initializes
         * HttpClient and ObjectMapper, but we override the methods that
         * actually make HTTP requests.</p>
         */
        public MockWorkoutClient() {
        }

        /**
         * Mock implementation of getAllWorkouts().
         * 
         * <p>Returns all workouts that have been "created" through this mock client,
         * simulating what the server would return from GET /api/workouts.</p>
         * 
         * @return list of all workouts stored in the mock
         * @throws IOException never thrown in mock implementation
         */
        @Override
        public List<Workout> getAllWorkouts() throws IOException {
            return new ArrayList<>(mockWorkouts);
        }

        /**
         * Mock implementation of createWorkout().
         * 
         * <p>Simulates server behavior:
         * 1. Assigns an ID to the workout (simulating server ID generation)
         * 2. Stores the workout in the mock's internal list
         * 3. Returns the workout with assigned ID (simulating server response)</p>
         * 
         * @param workout the workout to create
         * @return the workout with mock server-assigned ID
         * @throws IOException never thrown in mock implementation
         * @throws IllegalArgumentException if workout is null
         */
        @Override
        public Workout createWorkout(Workout workout) throws IOException {
            if (workout == null) {
                throw new IllegalArgumentException("Workout cannot be null");
            }

            // Simulate server assigning ID (like server WorkoutService does)
            Workout workoutWithId = workout;
            workoutWithId.setId(nextId++);

            // Simulate server storing the workout
            mockWorkouts.add(workoutWithId);

            // Return workout with ID (simulating server response)
            return workoutWithId;
        }

        /**
         * Mock implementation of updateWorkout().
         * 
         * <p>Not used in current tests, but required by WorkoutClient interface.</p>
         * 
         * @param id the ID of the workout to update
         * @param workout the updated workout data
         * @return the updated workout
         * @throws IOException never thrown in mock implementation
         */
        @Override
        public Workout updateWorkout(Long id, Workout workout) throws IOException {
            if (workout == null) {
                throw new IllegalArgumentException("Workout cannot be null");
            }
            
            // Simple mock: just update the workout with the ID and return it
            workout.setId(id);
            return workout;
        }

        /**
         * Mock implementation of deleteWorkout().
         * 
         * <p>Not used in current tests, but required by WorkoutClient interface.</p>
         * 
         * @param id the ID of the workout to delete
         * @throws IOException never thrown in mock implementation
         */
        @Override
        public void deleteWorkout(Long id) throws IOException {
            mockWorkouts.removeIf(w -> w.getId().equals(id));
        }
    }
}
