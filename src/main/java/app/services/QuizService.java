package app.services;

import app.dao.CocktailCandidate;
import app.dao.CocktailDAO;
import app.dto.CocktailDTO;
import app.dto.QuizAnswerDTO;
import app.exceptions.ApiException;
import app.strategies.ScoringStrategy;
import io.javalin.http.HttpStatus;

import java.util.List;

public class QuizService {

    private final CocktailDAO cocktailDAO;
    private final ScoringStrategy scoringStrategy;

    /**
     * Takes a ScoringStrategy instead of making it -
     * Chose the strategy pattern when QuizService is made =
     * Example: new QuizService(new EuclideanDistanceStrategy()).
     * Made so that new strategies for calculation of distance can be added in the future if needed.
      */

    public QuizService (ScoringStrategy scoringStrategy) {
        this.cocktailDAO = new CocktailDAO();
        this.scoringStrategy = scoringStrategy;
    }

    public CocktailDTO findBestMatch (QuizAnswerDTO answers) {
        List<CocktailCandidate> candidates = cocktailDAO.getCandidatesForQuiz(
                answers.getCocktailFamilyId(), answers.getSpiritId());

        if (candidates.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "No recipes matches the chosen cocktail-family/spirit-type");
        }

        CocktailCandidate bestMatch = null;
        double bestDistance = Double.MAX_VALUE;

        for (CocktailCandidate candidate : candidates) {
            double distance = scoringStrategy.calculateDistance(
                    answers.getTasteAnswers(), candidate.getFlavorProfile());

            if (distance < bestDistance) {
                bestDistance = distance;
                bestMatch = candidate;
            }
        }

        return CocktailDTO.fromEntity(bestMatch.getCocktail());
    }
}
