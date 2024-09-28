package org.mypetproject.recipeapi.domain.exception;

public class RecipeParsingException extends RuntimeException{
    public RecipeParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
