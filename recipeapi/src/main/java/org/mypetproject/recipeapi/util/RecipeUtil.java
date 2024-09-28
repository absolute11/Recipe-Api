package org.mypetproject.recipeapi.util;

import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.mappers.RecipeMapper;
import org.mypetproject.recipeapi.models.Recipe;
import org.mypetproject.recipeapi.repositories.RecipeRepository;

public class RecipeUtil {
    public static RecipeDTO saveRecipe(RecipeRepository recipeRepository, RecipeMapper recipeMapper, RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(savedRecipe);
    }
}
