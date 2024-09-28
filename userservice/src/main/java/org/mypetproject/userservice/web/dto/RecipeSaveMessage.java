package org.mypetproject.userservice.web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecipeSaveMessage implements Serializable {
    private String email;
    private String recipeTitle;
}