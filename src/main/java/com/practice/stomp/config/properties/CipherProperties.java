package com.practice.stomp.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherProperties {
    private static String seed;

    @Value("${cipher.seed}")
    public void setSeed(String seed) {
        CipherProperties.seed = seed;
    }

    public static String seed() {
        return seed;
    }
}
