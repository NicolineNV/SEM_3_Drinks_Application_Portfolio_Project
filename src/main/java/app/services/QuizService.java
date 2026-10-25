package app.services;

import app.dao.CocktailCandidateDAO;
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

    public QuizService (CocktailDAO cocktailDAO, ScoringStrategy scoringStrategy) {
        this.cocktailDAO = cocktailDAO;
        this.scoringStrategy = scoringStrategy;
    }

    public CocktailDTO findBestMatch (QuizAnswerDTO answers) {
        List<CocktailCandidateDAO> candidates = cocktailDAO.getCandidatesForQuiz(
                answers.getCocktailFamilyId(), answers.getSpiritId());

        if (candidates.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "No recipes match the chosen cocktail family/spirit type");
        }

        CocktailCandidateDAO bestMatch = null;
        double bestDistance = Double.MAX_VALUE;

        for (CocktailCandidateDAO candidate : candidates) {
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
