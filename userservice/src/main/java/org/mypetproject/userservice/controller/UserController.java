package org.mypetproject.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.mappers.UserRecipeMapper;
import org.mypetproject.userservice.security.JwtEntity;
import org.mypetproject.userservice.services.UserService;
import org.mypetproject.userservice.web.dto.RecipeDTO;
import org.mypetproject.userservice.web.dto.UserRecipeDTO;
import org.mypetproject.userservice.web.validation.OnCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRecipeMapper userRecipeMapper;
    @PostMapping("/recipes")
    public ResponseEntity<Void> createRecipeForUser(Authentication authentication, @RequestBody RecipeDTO recipeDTO) {
        JwtEntity userDetails = (JwtEntity) authentication.getPrincipal();
        Long userId = userDetails.getId();
        userService.createAndAssignRecipeToUser(userId, recipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/recipes/from-url")
    public ResponseEntity<Void> saveRecipeFromUrlForUser(Authentication authentication, @RequestParam String url) {
        JwtEntity userDetails = (JwtEntity) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long userId = userDetails.getId();
        userService.saveRecipeByUrlAndNotifyUser(userId,username,url);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<RecipeDTO>> getFavoriteRecipes(
            Authentication authentication,
            @RequestParam(required = false) String language) {
        log.info("Received language: {}", language);
        String username = authentication.getName();
        Long userId = userService.getByUsername(username).getId();
        List<RecipeDTO> favoriteRecipes = userService.getUserFavoriteRecipes(userId, language);
        return ResponseEntity.ok(favoriteRecipes);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRecipeFromFavorites(Authentication authentication, @RequestParam String recipeId) {
        JwtEntity userDetails = (JwtEntity) authentication.getPrincipal();
        Long userId = userDetails.getId();
        userService.removeRecipeFromFavorites(userId, recipeId);
        return ResponseEntity.noContent().build();
    }




}
