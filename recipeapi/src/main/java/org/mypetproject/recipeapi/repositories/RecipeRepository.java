package org.mypetproject.recipeapi.repositories;

import org.mypetproject.recipeapi.models.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe,String> {
}
