package org.mypetproject.recipeapi.recipeparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mypetproject.recipeapi.domain.exception.RecipeNotFoundException;
import org.mypetproject.recipeapi.domain.factory.JsoupConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecipeSearch {
    @Value("${search.url.template}")
    private String searchUrlTemplate;
    private final JsoupConnectionFactory connectionFactory;


    public String searchRecipeUrl(String dishName) throws IOException {
        String searchUrl = String.format(searchUrlTemplate, dishName.replace(" ", "+"));
        log.info("Searching URL: {}", searchUrl);

        Document doc;
        try {
            Connection connection = connectionFactory.createConnection(searchUrl);
            doc = connection.get();
        } catch (IOException e) {
            log.error("Failed to fetch URL: {}", searchUrl, e);
            throw e;
        }


        Element firstRecipeElement = doc.select("a.card").first(); // Обновленный селектор
        if (firstRecipeElement != null) {
            String recipeUrl = firstRecipeElement.attr("href");
            return recipeUrl;
        }

        throw new RecipeNotFoundException("Recipe not found for dish: " + dishName);
    }
}
