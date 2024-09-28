package org.mypetproject.userservice.util;

import org.mypetproject.userservice.entities.user.Role;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

public class UserUtil {

    public static void validateNewUser(UserRecipe user, UserRepository userRepository) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalStateException("Password and password confirmation do not match");
        }
    }

    public static UserRecipe prepareNewUser(UserRecipe user, PasswordEncoder passwordEncoder) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        return user;
    }
}