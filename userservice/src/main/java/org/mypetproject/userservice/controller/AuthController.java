package org.mypetproject.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.mappers.UserRecipeMapper;
import org.mypetproject.userservice.services.AuthService;
import org.mypetproject.userservice.services.UserService;
import org.mypetproject.userservice.web.dto.JwtRequest;
import org.mypetproject.userservice.web.dto.JwtResponse;
import org.mypetproject.userservice.web.dto.UserRecipeDTO;
import org.mypetproject.userservice.web.validation.OnCreate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    private final UserRecipeMapper userMapper;


    @PostMapping("/login")
    public JwtResponse login(@Validated @RequestBody
                             final JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public UserRecipeDTO register(@Validated(OnCreate.class) @RequestBody
                            final UserRecipeDTO userDto) {
        UserRecipe user = userMapper.toEntity(userDto);
        UserRecipe createdUser = userService.create(user);
        return userMapper.toDto(createdUser);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody final String refreshToken) {
        return authService.refresh(refreshToken);
    }

}
