package org.mypetproject.userservice.web.dto;

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