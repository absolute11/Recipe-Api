package org.mypetproject.recipeapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mypetproject.recipeapi.dtos.RecipeTitlesDTO;
import org.mypetproject.recipeapi.models.Recipe;

@Mapper(componentModel = "spring")
public interface RecipeTitlesMapper extends Mappeable<Recipe, RecipeTitlesDTO> {

}