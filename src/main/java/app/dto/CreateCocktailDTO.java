package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCocktailDTO {

    private String name;
    private String description;
    private String instructions;
    private Long cocktailFamilyId;
    private Long spiritId;
    private Long liqueurId;
    private Long mixerId;
    private Long syrupId;
    private Long garnishId;

}
