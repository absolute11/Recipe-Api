package org.mypetproject.translationservice.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mypetproject.translationservice.dtos.Recipe;
import org.mypetproject.translationservice.dtos.RecipeTitlesDTO;
import org.mypetproject.translationservice.utils.RecipeUpdater;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TranslationService {

    @Value("${yandex.iam.token}")
    private String iamToken;

    @Value("${yandex.folder.id}")
    private String folderId;

    @Value("${yandex.api.url}")
    private String apiUrl;

    public Recipe translateRecipe(Recipe recipe, String targetLanguageCode) {
        try {
            // Создаем JSON-объект запроса
            JSONObject requestBody = createRequestBodyForRecipe(
                    targetLanguageCode,
                    recipe.getTitle(),
                    recipe.getDescription(),
                    recipe.getIngredients(),
                    recipe.getSteps()
            );

            // Отправляем запрос на перевод и получаем результат
            JSONArray translations = sendTranslationRequest(requestBody);

            // Обновляем объект рецепта с переведенными данными
            return updateRecipeWithTranslations(recipe, translations);
        } catch (Exception e) {
            throw new RuntimeException("Error translating recipe", e);
        }
    }

    public RecipeTitlesDTO translateRecipeTitles(RecipeTitlesDTO recipeTitlesDTO, String targetLanguageCode) {
        try {
            // Создаем JSON-объект запроса
            JSONObject requestBody = createRequestBodyForTitles(
                    targetLanguageCode,
                    recipeTitlesDTO.getTitle(),
                    recipeTitlesDTO.getCategory()
            );

            // Отправляем запрос на перевод и получаем результат
            JSONArray translations = sendTranslationRequest(requestBody);

            // Обновляем объект RecipeTitlesDTO с переведенными данными
            return updateRecipeTitlesWithTranslations(recipeTitlesDTO, translations);
        } catch (Exception e) {
            throw new RuntimeException("Error translating recipe titles", e);
        }
    }

    // Создание тела запроса для перевода рецепта
    private JSONObject createRequestBodyForRecipe(String targetLanguageCode, String title, String description, List<String> ingredients, List<String> steps) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("folderId", folderId);

        JSONArray jsonTexts = new JSONArray();
        jsonTexts.put(title);
        jsonTexts.put(description);
        ingredients.forEach(jsonTexts::put);
        steps.forEach(jsonTexts::put);

        requestBody.put("texts", jsonTexts);
        requestBody.put("targetLanguageCode", targetLanguageCode);

        return requestBody;
    }

    // Создание тела запроса для перевода заголовков рецепта
    private JSONObject createRequestBodyForTitles(String targetLanguageCode, String title, String category) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("folderId", folderId);

        JSONArray jsonTexts = new JSONArray();
        jsonTexts.put(title);
        jsonTexts.put(category);

        requestBody.put("texts", jsonTexts);
        requestBody.put("targetLanguageCode", targetLanguageCode);

        return requestBody;
    }

    // Отправка запроса на перевод и получение результата
    private JSONArray sendTranslationRequest(JSONObject requestBody) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Translation request failed with status: " + responseEntity.getStatusCode());
        }

        JSONObject jsonResponse = new JSONObject(responseEntity.getBody());
        return jsonResponse.getJSONArray("translations");
    }

    // Создание заголовков HTTP-запроса
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + iamToken);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    // Обновление объекта Recipe с переведенными данными
    private Recipe updateRecipeWithTranslations(Recipe recipe, JSONArray translations) {
        return RecipeUpdater.updateRecipeWithTranslations(recipe, translations);
    }

    // Обновление объекта RecipeTitlesDTO с переведенными данными
    private RecipeTitlesDTO updateRecipeTitlesWithTranslations(RecipeTitlesDTO recipeTitlesDTO, JSONArray translations) {
        recipeTitlesDTO.setTitle(translations.getJSONObject(0).getString("text"));
        recipeTitlesDTO.setCategory(translations.getJSONObject(1).getString("text"));

        return recipeTitlesDTO;
    }
}