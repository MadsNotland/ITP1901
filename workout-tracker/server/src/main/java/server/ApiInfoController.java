package server;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for API root endpoint.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiInfoController {

    /**
     * Returns basic information about the API.
     *
     * @return Map containing API information
     */
    @GetMapping({"", "/"})
    public Map<String, Object> getApiInfo() {
        return Map.of(
            "name", "Workout Tracker REST API",
            "version", "1.0",
            "endpoints", Map.of(
                "workouts", "/api/workouts",
                "documentation", "See /docs/release3/rest-api.md"
            )
        );
    }
}
