package org.mypetproject.mailservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class RecipeSaveMessage {
    private String email;
    private String recipeTitle;
}