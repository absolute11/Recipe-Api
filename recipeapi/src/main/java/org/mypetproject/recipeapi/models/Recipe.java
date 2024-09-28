package org.mypetproject.recipeapi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "recipes")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Recipe {
    @Id
    private String id;

    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
    private Map<String, String> nutritionFacts;
    private String category;
    private String url;


}
