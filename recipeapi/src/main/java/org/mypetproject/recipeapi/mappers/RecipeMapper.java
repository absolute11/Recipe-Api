package org.mypetproject.recipeapi.mappers;

import org.mapstruct.Mapper;
import org.mypetproject.recipeapi.dtos.RecipeDTO;
import org.mypetproject.recipeapi.models.Recipe;

@Mapper(componentModel = "spring")
public interface RecipeMapper extends Mappeable<Recipe, RecipeDTO> {



}
