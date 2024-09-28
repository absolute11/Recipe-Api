package org.mypetproject.recipeapi.domain.factory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class JsoupConnectionFactory {
    public Connection createConnection(String url){
        return Jsoup.connect(url);
    }
}
