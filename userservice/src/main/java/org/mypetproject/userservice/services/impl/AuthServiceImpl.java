package org.mypetproject.userservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.security.JwtTokenProvider;
import org.mypetproject.userservice.services.AuthService;
import org.mypetproject.userservice.services.UserService;
import org.mypetproject.userservice.web.dto.JwtRequest;
import org.mypetproject.userservice.web.dto.JwtResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(JwtRequest loginRequest) {
        JwtResponse jwtResponse = new JwtResponse();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        UserRecipe user = userService.getByUsername(loginRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider
                .createAccessToken(user.getId(),
                        user.getUsername(), user.getRoles()));
        jwtResponse.setRefreshToken(jwtTokenProvider
                .createRefreshToken(user.getId(),
                        user.getUsername()));
        return jwtResponse;
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }
}
