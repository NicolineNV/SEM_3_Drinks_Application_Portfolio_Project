package app.strategies;

import java.util.Map;

public interface ScoringStrategy {
    double calculateDistance(Map<Long, Integer> userAnswers, Map<Long, Integer> cocktailProfile);
}
