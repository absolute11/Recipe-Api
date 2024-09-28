package org.mypetproject.translationservice.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class Recipe implements Serializable {
    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
    private Map<String,String> nutritionFacts;
    private String category;

}
