package org.mypetproject.recipeapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.dtos.RecipeTitlesDTO;
import org.mypetproject.recipeapi.mappers.RecipeMapper;
import org.mypetproject.recipeapi.models.Recipe;
import org.mypetproject.recipeapi.recipeparser.RecipeParser;
import org.mypetproject.recipeapi.services.MessageSender;
import org.mypetproject.recipeapi.services.RecipeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Recipe API", description = "API for managing recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final MessageSender messageSender;
    private final RecipeMapper recipeMapper;

    @QueryMapping(name = "recipesByName")
    @Operation(summary = "Search recipes by name", description = "Returns a list of recipes based on the provided dish name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of recipes"),
            @ApiResponse(responseCode = "404", description = "Recipes not found")
    })
    @Cacheable(value = "recipesByName", key = "#dishName + '_' + #language")
    @GetMapping("/search")
    public List<RecipeTitlesDTO> searchRecipesByName(@RequestParam @Argument String dishName, @Argument @RequestParam(required = false) String language) {
        List<RecipeTitlesDTO> recipes = recipeService.getRecipesByName(dishName);

        if (language != null && !language.isEmpty()) {
            for (int i = 0; i < recipes.size(); i++) {
                try {
                    RecipeTitlesDTO translatedRecipe = messageSender.requestRecipeTitleTranslation(recipes.get(i), language);
                    recipes.set(i, translatedRecipe);
                } catch (Exception e) {
                    throw new RuntimeException("Error during translation", e);
                }
            }
        }
        return recipes;
    }

    @QueryMapping(name = "recipeByUrl")
    @Operation(summary = "Get recipe by URL", description = "Fetches a recipe based on the provided URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipe"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @Cacheable(value = "recipeByUrl", key = "#url + '_' + #language")
    @GetMapping
    public RecipeDTO getRecipeByUrl(@RequestParam @Argument String url, @Argument @RequestParam(required = false) String language) {
        try {
            RecipeDTO recipeDTO = recipeService.getRecipeByUrl(url);

            if (language != null && !language.isEmpty()) {
                recipeDTO = messageSender.requestRecipeTranslation(recipeDTO, language);
            }
            return recipeDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error during translation", e);
        }
    }

    @MutationMapping(name = "saveRecipe")
    @Operation(summary = "Save a recipe", description = "Saves a new recipe to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully saved recipe"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @CacheEvict(value = {"recipesByName", "recipeByUrl"}, allEntries = true)
    @PostMapping
    public ResponseEntity<RecipeDTO> saveRecipe(@Argument @RequestBody @Validated RecipeDTO recipeDTO) {
        RecipeDTO savedRecipe = recipeService.saveRecipe(recipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    @MutationMapping(name = "deleteRecipe")
    @Operation(summary = "Delete a recipe from favorites", description = "Deletes a recipe from the list of favorite recipes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted recipe"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @CacheEvict(value = {"recipesByName", "recipeByUrl"}, key = "#id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipeFromFavorite(@PathVariable @Argument String id) {
        recipeService.deleteRecipeFromFavorite(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/save-and-assign")
    public ResponseEntity<Void> saveAndAssignRecipe(@RequestBody @Validated RecipeDTO recipeDTO, @RequestParam Long userId) {
        RecipeDTO savedRecipe = recipeService.saveRecipe(recipeDTO);
        Recipe recipeAfterSave = recipeMapper.toEntity(savedRecipe);
        recipeService.assignRecipeToUser(userId, recipeAfterSave.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getTitle")
    public String getTitle(@RequestParam String url) {
        return recipeService.getTitle(url);
    }

}
