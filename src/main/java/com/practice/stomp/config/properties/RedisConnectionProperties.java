package com.practice.stomp.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "redis")
@Component
public class RedisConnectionProperties {
    private String host;
    private Integer port;
    private Timeout timeout;

    @Getter
    @Setter
    public static class Timeout {
        private Integer connection;
        private Integer command;
        private Integer shutdown;
    }
}
