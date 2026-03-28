package com.coworking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProperties {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerUrl() {
        int protocolIndex = url.indexOf("//");
        if (protocolIndex < 0) {
            return url;
        }

        int hostStartIndex = protocolIndex + 2;
        int databasePathIndex = url.indexOf('/', hostStartIndex);
        if (databasePathIndex < 0) {
            return url.endsWith("/") ? url : url + "/";
        }

        String base = url.substring(0, databasePathIndex + 1);
        int queryIndex = url.indexOf('?');
        if (queryIndex >= 0) {
            return base + url.substring(queryIndex);
        }
        return base;
    }
}
