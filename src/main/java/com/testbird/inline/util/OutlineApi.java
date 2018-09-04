package com.testbird.inline.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @see <a href="https://github.com/Jigsaw-Code/outline-server/tree/master/src/shadowbox#access-keys-management-api">shadowbox rest api</a>
 */
@Component
@ConfigurationProperties("outline.server")
public class OutlineApi {
    private String host;
    private String port;
    private String key;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String listUsers() {
        return String.format("https://%s:%s/%s/access-keys/", host, port, key);
    }

    public String createUser() {
        return String.format("https://%s:%s/%s/access-keys/", host, port, key);
    }

    public String updateUserName(String userId) {
        return String.format("https://%s:%s/%s/access-keys/%s/name", host, port, key, userId);
    }

    public String deleteUser(String userId) {
        return String.format("https://%s:%s/%s/access-keys/%s", host, port, key, userId);
    }
}
