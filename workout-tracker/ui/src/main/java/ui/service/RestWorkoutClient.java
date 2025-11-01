package ui.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.Workout;

/**
 * Client for communicating with the workout REST API server.
 * 
 * <p>This class handles all HTTP communication with the server's REST API endpoints.
 * It provides methods for retrieving, creating, updating, and deleting workouts
 * via HTTP requests to {@code http://localhost:8080/api/workouts}.</p>
 * 
 * <p>The client uses Jackson ObjectMapper for JSON serialization and deserialization,
 * with JavaTimeModule support for proper LocalDate handling in Workout objects.</p>
 * 
 * <p>All methods throw {@link IOException} if network communication fails,
 * allowing callers to handle server connectivity issues appropriately.</p>
 */
public class RestWorkoutClient {

    private static final String BASE_URL = "http://localhost:8080/api/workouts";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new {@code RestWorkoutClient} with default HTTP client configuration.
     * 
     * <p>The HTTP client is configured with a 10-second timeout for all requests.
     * The ObjectMapper is configured with JavaTimeModule to handle LocalDate
     * serialization/deserialization correctly.</p>
     */
    public RestWorkoutClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Retrieves all workouts from the server.
     * 
     * @return a list of all workouts from the server
     * @throws IOException if the HTTP request fails or the server cannot be reached
     */
    public List<Workout> getAllWorkouts() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new IOException("Server returned status code: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Workout.class));
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request was interrupted", e);
        }
    }

    /**
     * Creates a new workout on the server.
     * 
     * <p>The server will assign an ID to the workout and return the created workout
     * with the assigned ID. The returned workout should be used instead of the original.</p>
     * 
     * @param workout the workout to create (ID is ignored, server will assign one)
     * @return the created workout with server-assigned ID
     * @throws IOException if the HTTP request fails or the server cannot be reached
     * @throws IllegalArgumentException if workout is null
     */
    public Workout createWorkout(Workout workout) throws IOException {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }

        String jsonBody = objectMapper.writeValueAsString(workout);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 201) {
                throw new IOException("Server returned status code: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), Workout.class);
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request was interrupted", e);
        }
    }

    /**
     * Updates an existing workout on the server.
     * 
     * @param id the ID of the workout to update
     * @param workout the updated workout data
     * @return the updated workout from the server
     * @throws IOException if the HTTP request fails, the server cannot be reached,
     *                     or the workout with the given ID is not found (404)
     * @throws IllegalArgumentException if workout is null
     */
    public Workout updateWorkout(Long id, Workout workout) throws IOException {
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null");
        }

        String jsonBody = objectMapper.writeValueAsString(workout);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new IOException("Workout with ID " + id + " not found");
            }
            if (response.statusCode() != 200) {
                throw new IOException("Server returned status code: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), Workout.class);
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request was interrupted", e);
        }
    }

    /**
     * Deletes a workout from the server.
     * 
     * @param id the ID of the workout to delete
     * @throws IOException if the HTTP request fails, the server cannot be reached,
     *                     or the workout with the given ID is not found (404)
     */
    public void deleteWorkout(Long id) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new IOException("Workout with ID " + id + " not found");
            }
            if (response.statusCode() != 204) {
                throw new IOException("Server returned status code: " + response.statusCode());
            }
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request was interrupted", e);
        }
    }
}

