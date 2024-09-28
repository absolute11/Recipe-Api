package org.mypetproject.userservice.services;

import org.mypetproject.userservice.web.dto.JwtRequest;
import org.mypetproject.userservice.web.dto.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest loginRequest);

    JwtResponse refresh(String refreshToken);
}
