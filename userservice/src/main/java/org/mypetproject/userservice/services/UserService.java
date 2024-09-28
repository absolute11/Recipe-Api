package org.mypetproject.userservice.services;

import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.web.dto.RecipeDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserService {
    UserRecipe getById(Long id);

    UserRecipe getByUsername(String username);

    UserRecipe update(UserRecipe user);

    UserRecipe create(UserRecipe user);
    void delete(Long id);
    void addRecipeToFavorites(Long userId, String recipeId);
    List<RecipeDTO> getUserFavoriteRecipes(Long userId, String language);
    void createAndAssignRecipeToUser(Long userId, RecipeDTO recipeDTO);
    void saveRecipeByUrlAndAssignToUser(Long userId, String url);
    RecipeDTO receiveTranslatedRecipe() throws ExecutionException, InterruptedException;
    RecipeDTO requestRecipeTranslation(RecipeDTO recipe, String targetLanguageCode) throws ExecutionException, InterruptedException;
    void removeRecipeFromFavorites(Long userId, String recipeId);
    void saveRecipeByUrlAndNotifyUser(Long userId, String url, String jwtToken);
}
