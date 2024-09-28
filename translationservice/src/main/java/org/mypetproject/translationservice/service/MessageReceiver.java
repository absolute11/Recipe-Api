package org.mypetproject.translationservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mypetproject.translationservice.dtos.Recipe;
import org.mypetproject.translationservice.dtos.RecipeMessage;
import org.mypetproject.translationservice.dtos.RecipeTitleMessage;
import org.mypetproject.translationservice.dtos.RecipeTitlesDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.mypetproject.translationservice.configuration.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReceiver {

    private final RabbitTemplate rabbitTemplate;
    private final TranslationService translationService;

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveRecipeMessage(RecipeMessage recipeMessage) {
        log.info("Received recipe message: {}", recipeMessage);
        try {
            Recipe translatedRecipe = handleRecipeTranslation(recipeMessage);
            sendTranslatedRecipe(translatedRecipe);
        } catch (Exception e) {
            log.error("Error processing RecipeMessage: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = TITLES_QUEUE_NAME)
    public void receiveRecipeTitleMessage(RecipeTitleMessage recipeTitleMessage) {
        log.info("Received recipe title message: {}", recipeTitleMessage);
        try {
            RecipeTitlesDTO translatedRecipeTitle = handleRecipeTitleTranslation(recipeTitleMessage);
            sendTranslatedRecipeTitle(translatedRecipeTitle);
        } catch (Exception e) {
            log.error("Error processing RecipeTitleMessage: {}", e.getMessage(), e);
        }
    }

    private Recipe handleRecipeTranslation(RecipeMessage recipeMessage) {
        Recipe recipe = recipeMessage.getRecipe();
        String targetLanguageCode = recipeMessage.getTargetLanguageCode();
        log.info("Translating recipe '{}' to language '{}'", recipe.getTitle(), targetLanguageCode);
        return translationService.translateRecipe(recipe, targetLanguageCode);
    }

    private RecipeTitlesDTO handleRecipeTitleTranslation(RecipeTitleMessage recipeTitleMessage) {
        RecipeTitlesDTO recipeTitle = recipeTitleMessage.getRecipeTitle();
        String targetLanguageCode = recipeTitleMessage.getTargetLanguageCode();
        log.info("Translating recipe title '{}' to language '{}'", recipeTitle.getTitle(), targetLanguageCode);
        return translationService.translateRecipeTitles(recipeTitle, targetLanguageCode);
    }

    private void sendTranslatedRecipe(Recipe translatedRecipe) {
        log.info("Sending translated recipe to response queue: {}", translatedRecipe);
        rabbitTemplate.convertAndSend(RESPONSE_QUEUE_NAME, translatedRecipe);
    }

    private void sendTranslatedRecipeTitle(RecipeTitlesDTO translatedRecipeTitle) {
        log.info("Sending translated recipe title to response queue: {}", translatedRecipeTitle);
        rabbitTemplate.convertAndSend(TITLES_RESPONSE_QUEUE_NAME, translatedRecipeTitle);
    }
}