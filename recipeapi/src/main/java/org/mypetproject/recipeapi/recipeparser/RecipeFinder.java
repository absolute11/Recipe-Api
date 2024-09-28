package org.mypetproject.recipeapi.recipeparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mypetproject.recipeapi.domain.factory.JsoupConnectionFactory;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.mappers.RecipeMapper;
import org.mypetproject.recipeapi.models.Recipe;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Component
@RequiredArgsConstructor
@Slf4j
public class RecipeFinder {
    private final JsoupConnectionFactory connectionFactory;
    private final RecipeParser parser;
    private final RecipeMapper recipeMapper;

    public List<Recipe> findAllRecipes(String pageUrl) throws IOException {
        Connection connection = connectionFactory.createConnection(pageUrl);
        Document doc = connection.get();

        Elements recipeLinks = doc.select("a.mntl-card-list-items");

        List<Recipe> recipes = new ArrayList<>();

        for (Element link : recipeLinks) {
            String recipeUrl = link.attr("href");
            try {
                RecipeDTO recipeDTO = parser.parseRecipe(recipeUrl);
                Recipe recipeEntity = recipeMapper.toEntity(recipeDTO);
                recipeEntity.setUrl(recipeUrl);
                recipes.add(recipeEntity);
                log.info("Successfully parsed recipe: {}", recipeEntity.getTitle());
            } catch (Exception e) {
                log.error("Failed to parse recipe at URL: {}", recipeUrl, e);
            }
        }

        return recipes;
    }
}