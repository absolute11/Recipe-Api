package org.mypetproject.userservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO implements Serializable {

    private String title;

    private String description;

    private List<String> ingredients;

    private List<String> steps;

    private Map<String, String> nutritionFacts;

    private String category;
}
