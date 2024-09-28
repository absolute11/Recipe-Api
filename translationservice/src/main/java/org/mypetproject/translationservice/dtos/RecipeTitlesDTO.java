package org.mypetproject.translationservice.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecipeTitlesDTO implements Serializable {

    private String title;
    private String category;
    private String url;
}
