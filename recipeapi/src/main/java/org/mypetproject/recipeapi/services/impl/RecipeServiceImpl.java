package org.mypetproject.recipeapi.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.recipeapi.domain.exception.RecipeNotFoundException;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.dtos.RecipeTitlesDTO;
import org.mypetproject.recipeapi.mappers.RecipeMapper;
import org.mypetproject.recipeapi.mappers.RecipeTitlesMapper;
import org.mypetproject.recipeapi.models.Recipe;
import org.mypetproject.recipeapi.recipeparser.RecipeFinder;
import org.mypetproject.recipeapi.recipeparser.RecipeParser;
import org.mypetproject.recipeapi.recipeparser.RecipeSearch;
import org.mypetproject.recipeapi.repositories.RecipeRepository;
import org.mypetproject.recipeapi.services.RecipeService;
import org.mypetproject.recipeapi.util.RecipeUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static org.mypetproject.recipeapi.configuration.RabbitMQConfig.USER_RECIPE_REQUEST_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeSearch recipeSearch;
    private final RecipeFinder recipeFinder;
    private final RecipeParser parser;
    private final RecipeTitlesMapper recipeTitleMapper;
    private final RecipeMapper recipeMapper;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public List<RecipeTitlesDTO> getRecipesByName(String dishName) {
        try {
            String searchUrl = recipeSearch.searchRecipeUrl(dishName);
            List<RecipeTitlesDTO> recipes = recipeTitleMapper.toDto(recipeFinder.findAllRecipes(searchUrl));
            log.info("Found {} recipes for dish name: {}", recipes.size(), dishName);
            return recipes;
        } catch (IOException e) {
            log.error("Error finding recipes for dish name: {}", dishName, e);
            throw new RecipeNotFoundException("Error finding recipes for dish: " + dishName);
        }
    }

    @Override
    public RecipeDTO getRecipeByUrl(String url) {
        log.info("Fetching recipe by URL: {}", url);
        RecipeDTO recipeDTO = parser.parseRecipe(url);
        log.info("Successfully fetched recipe: {}", recipeDTO.getTitle());
        return recipeDTO;
    }



    @Override
    public RecipeDTO saveRecipe(RecipeDTO recipeDTO) {
        log.info("Saving recipe: {}", recipeDTO.getTitle());
        RecipeDTO savedRecipe = RecipeUtil.saveRecipe(recipeRepository, recipeMapper, recipeDTO);
        return savedRecipe;
    }
    @Override
    public void assignRecipeToUser(Long userId, String recipeId) {
        // Формируем сообщение и отправляем его в RabbitMQ, чтобы User Service добавил рецепт в избранное
        String message = userId + ":" + recipeId;
        rabbitTemplate.convertAndSend(USER_RECIPE_REQUEST_QUEUE, message);
    }

    @Override
    public String getTitle(String url) {
        return parser.parseRecipe(url).getTitle();
    }


    @Override
    public void deleteRecipeFromFavorite(String id) {
        log.info("Deleting recipe with ID: {}", id);
        recipeRepository.deleteById(id);
        log.info("Successfully deleted recipe with ID: {}", id);
    }
}
