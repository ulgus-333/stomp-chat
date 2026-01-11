package com.practice.stomp.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oci")
public class OciProperties {
    public static Duration WRITE_EXPIRE_DURATION = Duration.ofMinutes(5);
    public static Duration READ_EXPIRE_DURATION = Duration.ofDays(1);
    private static String URL_PREFIX = "https://objectstorage.%s.oraclecloud.com%s";

    private String namespace;
    private String region;
    private String bucket;

    public String preAuthenticatedRequestUrl(String accessUri) {
        return String.format(URL_PREFIX, region, accessUri);
    }
}
