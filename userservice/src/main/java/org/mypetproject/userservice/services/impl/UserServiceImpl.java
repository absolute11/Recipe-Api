package org.mypetproject.userservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.userservice.domain.exception.ResourceNotFoundException;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.entities.user.Role;
import org.mypetproject.userservice.repositories.UserRepository;
import org.mypetproject.userservice.services.UserService;
import org.mypetproject.userservice.util.UserUtil;
import org.mypetproject.userservice.web.dto.RecipeDTO;

import org.mypetproject.userservice.web.dto.RecipeMessage;
import org.mypetproject.userservice.web.dto.RecipeSaveMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mypetproject.userservice.configuration.RabbitMQConfig.*;



@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public UserRecipe getById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserRecipe getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserRecipe update(UserRecipe user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public UserRecipe create(UserRecipe user) {
        UserUtil.validateNewUser(user, userRepository);
        UserRecipe preparedUser = UserUtil.prepareNewUser(user, passwordEncoder);
        UserRecipe savedUser = userRepository.save(preparedUser);
        rabbitTemplate.convertAndSend(REGISTRATION_EMAIL_QUEUE, preparedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addRecipeToFavorites(Long userId, String recipeId) {
        UserRecipe user = getById(userId);
        if (user.getRecipeIds().contains(recipeId)) {
            throw new IllegalStateException("Recipe is already in favorites");
        }
        user.getRecipeIds().add(recipeId);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getUserFavoriteRecipes(Long userId, String language) {
        UserRecipe user = getById(userId);
        List<RecipeDTO> recipes = new ArrayList<>();
        log.info("Received language: {}", language);
        for (String recipeId : user.getRecipeIds()) {
            RecipeDTO recipe = (RecipeDTO) rabbitTemplate.convertSendAndReceive(RECIPE_GET_QUEUE, recipeId);
            log.info("Received language: {}", language);
            if (language != null && !language.isEmpty()) {
                try {
                    log.info("Language before translation: {}", language);
                    recipe = requestRecipeTranslation(recipe, language);
                } catch (Exception e) {
                    throw new RuntimeException("Error translating recipe", e);
                }
            }
            recipes.add(recipe);
        }
        return recipes;
    }
    @Override
    @Transactional
    public void removeRecipeFromFavorites(Long userId, String recipeId) {
        UserRecipe user = getById(userId);
        if (!user.getRecipeIds().contains(recipeId)) {
            throw new IllegalStateException("Recipe not found in favorites");
        }
        user.getRecipeIds().remove(recipeId);
        userRepository.save(user);
        String recipeServiceUrl = "http://localhost:8080/api/recipes/" + recipeId;
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.delete(recipeServiceUrl);
            log.info("Recipe with ID {} successfully deleted from Recipe Service", recipeId);
        } catch (Exception e) {
            log.error("Failed to delete recipe with ID {} from Recipe Service", recipeId, e);
            throw new RuntimeException("Error deleting recipe from Recipe Service", e);
        }
    }

    @Override
    public void createAndAssignRecipeToUser(Long userId, RecipeDTO recipeDTO) {
        rabbitTemplate.convertAndSend(USER_RECIPE_DTO_QUEUE, recipeDTO);
        String recipeId = (String) rabbitTemplate.receiveAndConvert(USER_RECIPE_RESPONSE_QUEUE);
        addRecipeToFavorites(userId, recipeId);
    }

    @Override
    public void saveRecipeByUrlAndAssignToUser(Long userId, String url) {
        rabbitTemplate.convertAndSend(USER_RECIPE_URL_QUEUE, url);
        String recipeId = (String) rabbitTemplate.receiveAndConvert(USER_RECIPE_RESPONSE_QUEUE);
        addRecipeToFavorites(userId, recipeId);

    }

    @Override
    public void saveRecipeByUrlAndNotifyUser(Long userId,String email, String url) {
        // Сохраняем рецепт и добавляем его в избранное
        saveRecipeByUrlAndAssignToUser(userId, url);
        String recipeUrlTitle = "http://localhost:8080/api/recipes/getTitle?url=" + url;
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Отправляем запрос на получение названия рецепта
            ResponseEntity<String> response = restTemplate.getForEntity(recipeUrlTitle, String.class);
            String recipeTitle = response.getBody();


            // Логирование успешного получения названия рецепта
            log.info("Successfully retrieved recipe title: {}", recipeTitle);

            // Отправляем уведомление
            RecipeSaveMessage message = new RecipeSaveMessage();
            message.setEmail(email);
            message.setRecipeTitle(recipeTitle);
            rabbitTemplate.convertAndSend(RECIPE_SAVE_EMAIL_QUEUE, message);
        } catch (Exception e) {
            log.error("Failed to retrieve recipe title from Recipe Service", e);
            throw new RuntimeException("Error retrieving recipe title from Recipe Service", e);
        }

    }

    @Override
    public RecipeDTO requestRecipeTranslation(RecipeDTO recipe, String language) throws ExecutionException, InterruptedException {
        RecipeMessage message = new RecipeMessage(recipe, language);
        log.info("Language before sending to RabbitMQ: {}", message.getTargetLanguageCode());
        rabbitTemplate.convertAndSend(REQUEST_QUEUE_NAME, message);
        return receiveTranslatedRecipe();
    }

    @Override
    public RecipeDTO receiveTranslatedRecipe() throws ExecutionException, InterruptedException {
        CompletableFuture<RecipeDTO> future = new CompletableFuture<>();
        rabbitTemplate.setReceiveTimeout(10000);
        RecipeDTO translatedRecipe = (RecipeDTO) rabbitTemplate.receiveAndConvert(RESPONSE_QUEUE_NAME);
        if (translatedRecipe != null) {
            future.complete(translatedRecipe);
        } else {
            future.completeExceptionally(new RuntimeException("Translation failed or timed out for RecipeDTO."));
        }
        return future.get();
    }
}