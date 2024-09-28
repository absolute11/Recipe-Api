package org.mypetproject.userservice.mappers;

import org.mapstruct.Mapper;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.web.dto.UserRecipeDTO;

@Mapper(componentModel = "spring")
public interface UserRecipeMapper extends Mappable<UserRecipe, UserRecipeDTO> {
}
