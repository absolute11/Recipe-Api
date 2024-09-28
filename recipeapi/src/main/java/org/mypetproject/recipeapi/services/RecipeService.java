package org.mypetproject.recipeapi.services;

import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.dtos.RecipeTitlesDTO;

import java.util.List;

public interface RecipeService {
    List<RecipeTitlesDTO> getRecipesByName(String name);
    RecipeDTO getRecipeByUrl(String url);



    RecipeDTO saveRecipe(RecipeDTO recipeDTO);

    void deleteRecipeFromFavorite(String id);

    void assignRecipeToUser(Long userId, String recipeId);
    String getTitle(String url);

}
