package org.mypetproject.userservice.security;

import lombok.RequiredArgsConstructor;
import org.mypetproject.userservice.entities.user.UserRecipe;
import org.mypetproject.userservice.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        UserRecipe user = userService.getByUsername(username);
        return JwtEntityFactory.create(user);
    }
}
