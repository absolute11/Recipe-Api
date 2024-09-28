package org.mypetproject.translationservice.utils;

import org.json.JSONArray;
import org.mypetproject.translationservice.dtos.Recipe;

public class RecipeUpdater {

    public static Recipe updateRecipeWithTranslations(Recipe recipe, JSONArray translations) {
        recipe.setTitle(translations.getJSONObject(0).getString("text"));
        recipe.setDescription(translations.getJSONObject(1).getString("text"));

        updateIngredients(recipe, translations);
        updateSteps(recipe, translations);

        return recipe;
    }

    private static void updateIngredients(Recipe recipe, JSONArray translations) {
        int index = 2;
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            recipe.getIngredients().set(i, translations.getJSONObject(index++).getString("text"));
        }
    }

    private static void updateSteps(Recipe recipe, JSONArray translations) {
        int index = recipe.getIngredients().size() + 2; // Start after ingredients
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            recipe.getSteps().set(i, translations.getJSONObject(index++).getString("text"));
        }
    }
}