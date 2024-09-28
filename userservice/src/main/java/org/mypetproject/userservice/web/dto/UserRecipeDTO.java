package org.mypetproject.userservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.mypetproject.userservice.web.validation.OnCreate;
import org.mypetproject.userservice.web.validation.OnUpdate;

@Getter
@Setter
public class UserRecipeDTO {
    @NotNull(message = "name must be not null.",
            groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255, message = "Name length must be smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotNull(message = "Username must be not null.",
            groups = {OnUpdate.class, OnCreate.class})
    @Length(max = 255, message = "Username length must be "
            + "smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class})
    private String username;

    @NotNull(message = "password must be not null",
            groups = {OnCreate.class, OnUpdate.class})
    @JsonProperty(access = JsonProperty
            .Access.WRITE_ONLY)
    private String password;
    @NotNull(message = "password confirmation must be not null",
            groups = {OnCreate.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordConfirmation;
}
