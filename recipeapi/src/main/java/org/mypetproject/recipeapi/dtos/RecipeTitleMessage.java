package org.mypetproject.recipeapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class RecipeTitleMessage implements Serializable {
    private RecipeTitlesDTO recipeTitle;
    private String targetLanguageCode;
}
