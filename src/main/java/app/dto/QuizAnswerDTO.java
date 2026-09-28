package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDTO {

    private Long cocktailFamilyId;
    private Long spiritId; // null = no preferences, all spirit types is accepted
    private Map<Long, Integer> tasteAnswers; // flavorTagId -> wished intensity (1-5)

}
