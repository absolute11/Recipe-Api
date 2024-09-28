package org.mypetproject.recipeapi.recipeparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mypetproject.recipeapi.domain.exception.RecipeParsingException;
import org.mypetproject.recipeapi.domain.factory.JsoupConnectionFactory;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecipeParser {
    private final JsoupConnectionFactory connectionFactory;

    public RecipeDTO parseRecipe(String url) {
        try {

            Connection connection = connectionFactory.createConnection(url);
            Document doc = connection.get();

            return RecipeDTO.builder()
                    .title(doc.title())
                    .description(extractDescription(doc))
                    .ingredients(extractIngredients(doc))
                    .steps(extractSteps(doc))
                    .nutritionFacts(extractNutritionFacts(doc))
                    .category(extractCategory(doc))
                    .build();
        } catch (IOException e) {
            log.error("Failed to parse recipe from URL: {}", url, e);
            throw new RecipeParsingException("Failed to parse recipe from URL: " + url, e);
        }
    }
    private String extractDescription(Document doc) {
        Element metaDescription = doc.selectFirst("meta[name=description]");
        return metaDescription != null ? metaDescription.attr("content") : null;
    }

    private List<String> extractIngredients(Document doc) {
        return doc.select("ul.mm-recipes-structured-ingredients__list li.mm-recipes-structured-ingredients__list-item")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private List<String> extractSteps(Document doc) {
        Element stepsElement = doc.selectFirst("div.mm-recipes-steps__content");
        return stepsElement == null ? Collections.emptyList() :
                stepsElement.select("li").stream()
                        .map(Element::text)
                        .collect(Collectors.toList());
    }

    private Map<String, String> extractNutritionFacts(Document doc) {
        Map<String, String> nutritionFacts = new HashMap<>();
        Element nutritionElement = doc.selectFirst("div#mm-recipes-nutrition-facts-summary_1-0");
        if (nutritionElement != null) {
            Elements rows = nutritionElement.select("tbody.mm-recipes-nutrition-facts-summary__table-body tr");
            for (Element row : rows) {
                String value = row.select("td.mm-recipes-nutrition-facts-summary__table-cell.type--dog-bold").text();
                String nutrient = row.select("td.mm-recipes-nutrition-facts-summary__table-cell.type--dog").text();
                nutritionFacts.put(nutrient, value);
            }
        }
        return nutritionFacts;
    }

    private String extractCategory(Document doc) {
        Elements breadcrumbItems = doc.select("li.mntl-breadcrumbs__item");
        if (breadcrumbItems.size() > 1) {
            Element secondBreadcrumbItem = breadcrumbItems.get(1);
            Element categoryElement = secondBreadcrumbItem.selectFirst("span.link__wrapper");
            if (categoryElement != null) {
                return categoryElement.text();
            } else {
                log.warn("Category element not found in the second breadcrumb item");
            }
        } else {
            log.warn("Less than two breadcrumb items found");
        }
        return null;
    }

}
