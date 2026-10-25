package app.controllers;

import app.dao.CocktailDAO;
import app.dto.QuizAnswerDTO;
import app.services.QuizService;
import app.strategies.EuclideanDistanceStrategy;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class QuizController {

    private final QuizService quizService;

    public QuizController(EntityManagerFactory emf) {
        this.quizService = new QuizService(new CocktailDAO(emf), new EuclideanDistanceStrategy());
    }

    public void findMatch(Context ctx) {
        QuizAnswerDTO answers = ctx.bodyValidator(QuizAnswerDTO.class)
                .check(a -> a.getCocktailFamilyId() != null, "cocktailFamilyId is required")
                .check(a -> a.getTasteAnswers() != null && !a.getTasteAnswers().isEmpty(),
                        "tasteAnswers is required")
                .check(a -> a.getTasteAnswers() == null || a.getTasteAnswers().values().stream()
                        .allMatch(v -> v != null && v >= 1 && v <= 5),
                "every taste answer must be between 1 and 5")
                .get();

        ctx.json(quizService.findBestMatch(answers));
    }
}
