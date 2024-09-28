package org.mypetproject.userservice.repositories;

import org.mypetproject.userservice.entities.user.UserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRecipe,Long> {
    Optional<UserRecipe> findByUsername(String username);
}
