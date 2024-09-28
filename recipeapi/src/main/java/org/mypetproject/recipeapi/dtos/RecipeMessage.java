package org.mypetproject.recipeapi.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipeMessage implements Serializable {
    private RecipeDTO recipe;
    private String targetLanguageCode;
}