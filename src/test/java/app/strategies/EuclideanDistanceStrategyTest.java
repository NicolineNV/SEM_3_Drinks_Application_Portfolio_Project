package app.strategies;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EuclideanDistanceStrategyTest {

    private final EuclideanDistanceStrategy strategy = new EuclideanDistanceStrategy();

    @Test
    void identicalProfilesHaveZeroDistance() {
        Map<Long, Integer> profile = Map.of(1L, 5, 2L, 3);

        assertEquals(0.0, strategy.calculateDistance(profile, profile), 0.0001);
        // delta parameter needed because double-calculations can have microscopic differences in result
    }

    @Test
    void calculateKnownDistanceCorrectly() {
        Map<Long, Integer> userAnswers = Map.of(1L, 5, 2L, 1);
        Map<Long, Integer> cocktailProfile = Map.of(1L, 3, 2L, 1);
        // Difference: dim1 = 5-3=2, dim2 = 1-1=0 → √(2² + 0²) = √4 = 2.0

        assertEquals(2.0, strategy.calculateDistance(userAnswers, cocktailProfile), 0.0001);
    }

    @Test
    // Most important test - tests that a NullPointerException is not thrown if some dimensions are missing
    void missingDimensionCountsAsZero() {
        Map<Long, Integer> userAnswers = Map.of(1L, 5);
        Map<Long, Integer> cocktailProfile = Map.of(2L, 5);
        // dim1: 5-0=5, dim2: 0-5=-5 → √(25+25) = √50 ≈ 7.0711

        assertEquals(Math.sqrt(50), strategy.calculateDistance(userAnswers, cocktailProfile), 0.0001);
    }
}
