package org.mypetproject.recipeapi.services;


import lombok.RequiredArgsConstructor;

import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.dtos.RecipeMessage;
import org.mypetproject.recipeapi.dtos.RecipeTitleMessage;
import org.mypetproject.recipeapi.dtos.RecipeTitlesDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mypetproject.recipeapi.configuration.RabbitMQConfig.*;


@Service
@RequiredArgsConstructor
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public RecipeDTO requestRecipeTranslation(RecipeDTO recipe, String targetLanguageCode) throws ExecutionException, InterruptedException {
        System.out.println("Начало requestTranslation метода для RecipeDTO.");
        RecipeMessage message = RecipeMessage.builder()
                .recipe(recipe)
                .targetLanguageCode(targetLanguageCode)
                .build();

        System.out.println(message);
        rabbitTemplate.convertAndSend(REQUEST_QUEUE_NAME, message);
        System.out.println("Сообщение отправлено в очередь для RecipeDTO.");

        return receiveTranslatedRecipe();
    }

    public RecipeTitlesDTO requestRecipeTitleTranslation(RecipeTitlesDTO recipeTitle, String targetLanguageCode) throws ExecutionException, InterruptedException {
        System.out.println("Начало requestTranslation метода для RecipeTitlesDTO.");
        RecipeTitleMessage message = new RecipeTitleMessage(recipeTitle, targetLanguageCode);

        rabbitTemplate.convertAndSend(TITLES_REQUEST_QUEUE_NAME, message);
        System.out.println("Сообщение отправлено в очередь для RecipeTitlesDTO.");

        return receiveTranslatedRecipeTitle();
    }

    private RecipeDTO receiveTranslatedRecipe() throws ExecutionException, InterruptedException {
        CompletableFuture<RecipeDTO> future = new CompletableFuture<>();
        System.out.println("Сюда зашли");
        rabbitTemplate.setReceiveTimeout(10000);

        RecipeDTO translatedRecipe = (RecipeDTO) rabbitTemplate.receiveAndConvert(RESPONSE_QUEUE_NAME);
        System.out.println("Прошли");

        if (translatedRecipe != null) {
            System.out.println("Сообщение получено и распарсено для RecipeDTO.");
            future.complete(translatedRecipe);
        } else {
            future.completeExceptionally(new RuntimeException("Translation failed or timed out for RecipeDTO."));
        }

        return future.get();
    }

    private RecipeTitlesDTO receiveTranslatedRecipeTitle() throws ExecutionException, InterruptedException {
        CompletableFuture<RecipeTitlesDTO> future = new CompletableFuture<>();

        rabbitTemplate.setReceiveTimeout(10000);
        RecipeTitlesDTO translatedRecipeTitle = (RecipeTitlesDTO) rabbitTemplate.receiveAndConvert(TITLES_RESPONSE_QUEUE_NAME);

        if (translatedRecipeTitle != null) {
            System.out.println("Сообщение получено и распарсено для RecipeTitlesDTO.");
            future.complete(translatedRecipeTitle);
        } else {
            future.completeExceptionally(new RuntimeException("Translation failed or timed out for RecipeTitlesDTO."));
        }

        return future.get();
    }
}