package org.mypetproject.translationservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Builder
@Data
public class RecipeMessage implements Serializable {
    private Recipe recipe;
    private String targetLanguageCode;
}