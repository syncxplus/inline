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
    private String context;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String listUsers() {
        return String.format("https://%s:%s/%s/access-keys/", host, port, context);
    }

    public String createUser() {
        return String.format("https://%s:%s/%s/access-keys/", host, port, context);
    }

    public String createMultiUser(int count) {
        return String.format("https://%s:%s/%s/access-keys/%d", host, port, context, count);
    }

    public String updateUserName(String userId) {
        return String.format("https://%s:%s/%s/access-keys/%s/name", host, port, context, userId);
    }

    public String deleteUser(String userId) {
        return String.format("https://%s:%s/%s/access-keys/%s", host, port, context, userId);
    }

    public String deleteUsers() {
        return String.format("https://%s:%s/%s/access-keys", host, port, context);
    }

    public String serverStatus() {
        return String.format("https://%s:%s/%s/server", host, port, context);
    }

    public String enableMetrics() {
        return String.format("https://%s:%s/%s/metrics/enabled", host, port, context);
    }

    public String userStats() {
        return String.format("https://%s:%s/%s/metrics/transfer", host, port, context);
    }

    public String info() {
        return String.format("https://%s:%s/%s/info", host, port, context);
    }
}
