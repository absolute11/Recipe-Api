package org.mypetproject.recipeapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.dtos.RecipeMessage;
import org.mypetproject.recipeapi.mappers.RecipeMapper;
import org.mypetproject.recipeapi.models.Recipe;
import org.mypetproject.recipeapi.recipeparser.RecipeParser;
import org.mypetproject.recipeapi.repositories.RecipeRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static org.mypetproject.recipeapi.configuration.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeMessageHandler {

    private final RecipeRepository recipeRepository;
    private final RecipeParser recipeParser;
    private final RecipeMapper recipeMapper;
    private final RabbitTemplate rabbitTemplate;
    private final MessageSender messageSender;


    @RabbitListener(queues = USER_RECIPE_DTO_QUEUE)
    public void handleRecipeCreation(RecipeDTO recipeDTO) {
        // Сохраняем рецепт в MongoDB
        Recipe savedRecipe = recipeRepository.save(recipeMapper.toEntity(recipeDTO));

        // Отправляем ID рецепта обратно в очередь
        rabbitTemplate.convertAndSend(USER_RECIPE_RESPONSE_QUEUE, savedRecipe.getId());
    }

    @RabbitListener(queues = USER_RECIPE_URL_QUEUE)
    public void handleRecipeParsing(String url) {
        // Парсим рецепт по URL
        RecipeDTO recipeDTO = recipeParser.parseRecipe(url);

        // Сохраняем рецепт в MongoDB
        Recipe savedRecipe = recipeRepository.save(recipeMapper.toEntity(recipeDTO));

        // Отправляем ID рецепта обратно в очередь
        rabbitTemplate.convertAndSend(USER_RECIPE_RESPONSE_QUEUE, savedRecipe.getId());
    }

    @RabbitListener(queues = RECIPE_GET_QUEUE)
    public RecipeDTO handleGetRecipeRequest(String recipeId) {
        // Получаем рецепт из MongoDB по ID
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        log.info("Handling get request for Recipe ID: {}", recipeId);
        if (recipe == null) {
            log.error("Recipe not found: {}", recipeId);
            return null;
        }
        // Преобразуем рецепт в DTO и возвращаем его
        return recipeMapper.toDto(recipe);
    }
    @RabbitListener(queues = RECIPE_TRANSLATE_QUEUE)
    public void handleRecipeTranslationRequest(RecipeMessage recipeMessage) throws ExecutionException, InterruptedException {
        System.out.println(recipeMessage.getTargetLanguageCode());
        log.info("язык : " + recipeMessage.getTargetLanguageCode());
        System.out.println(recipeMessage.getRecipe());
        RecipeDTO translatedRecipe = messageSender.requestRecipeTranslation(recipeMessage.getRecipe(),recipeMessage.getTargetLanguageCode());


        // Отправляем переведенный рецепт обратно в очередь
        rabbitTemplate.convertAndSend(RESPONSE_QUEUE_NAME, translatedRecipe);
    }

}