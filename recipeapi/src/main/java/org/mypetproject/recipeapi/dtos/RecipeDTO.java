package org.mypetproject.recipeapi.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.mypetproject.recipeapi.validation.OnCreate;
import org.mypetproject.recipeapi.validation.OnUpdate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO implements Serializable {

    @Schema(description = "title", example = "Simple Chocolate Chip Cookies")
    @NotNull(message = "title must be not null")
    @Length(max = 255, message = "Title length must be "
            + "smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @Schema(description = "description", example = "These classic chocolate chip cookies are soft, chewy, and absolutely delicious. They are easy to make and perfect for any occasion!")
    @Length(max = 255, message = "Title length must be "
            + "smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class})
    private String description;
    @Schema(description = "ingredients", example = "1 cup unsalted butter, softened 1 cup white sugar ,1 cup packed brown sugar")
    @NotNull(message = "ingredients must be not null")
    private List<String> ingredients;
    @NotNull(message = "steps must be not null")
    @Schema(description = "steps", example = "Preheat the oven to 350 degrees F (175 degrees C). Line a baking sheet with parchment paper.")
    private List<String> steps;
    @NotNull(message = "nutrition facts must be not null")
    @Schema(description = "nutrition facts", example = "\"Fat\": \"10g\",\n" +
            "        \"Carbs\": \"22g\",\n" +
            "        \"Calories\": \"200\",\n" +
            "        \"Protein\": \"2g\"")
    private Map<String, String> nutritionFacts;
    @Schema(description = "category", example = "Desserts")
    @NotNull(message = "category must be not null")
    private String category;

}
