package app.strategies;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the "straight-line distance" between two flavor profiles.
 * (Pythagoras generalized to 5 dimensions: sweet, sour, bitter, salty, spicy).
 * Lower distance = better match!
 * Example: two identical profiles result in a distance of 0.
 * Reason for name: Euclidean Distance is the Greek name of this mathematical equation.
 * Another also correct name could be StraightLineDistanceStrategy
 */

public class EuclideanDistanceStrategy implements ScoringStrategy {

    @Override
    public double calculateDistance(Map<Long, Integer> userAnswers, Map<Long, Integer> cocktailProfile) {
        Set<Long> allFlavorTagIds = new HashSet<>();
        allFlavorTagIds.addAll(userAnswers.keySet());
        allFlavorTagIds.addAll(cocktailProfile.keySet());

        double sumOfSquares = 0;
        for (Long tagId : allFlavorTagIds){
            int userValue = userAnswers.getOrDefault(tagId, 0);
            int cocktailValue = cocktailProfile.getOrDefault(tagId, 0);
            int difference = userValue - cocktailValue;
            sumOfSquares += (double) difference * difference;
        }
        return Math.sqrt(sumOfSquares);
    }
}
