package org.mypetproject.recipeapi.mappers;

import java.util.List;

public interface Mappeable<E,D> {
    D toDto(E entity);

    List<D> toDto(List<E> entity);

    E toEntity(D dto);

    List<E> toEntity(List<D> dtos);
}
