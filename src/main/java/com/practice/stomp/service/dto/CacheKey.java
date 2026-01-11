package com.practice.stomp.service.dto;

import java.time.Duration;

public enum CacheKey {
    OCI_PAR_KEY(CacheKeyPrefix.OCI, "PAR::%s::%s", 2, Duration.ofMinutes(5L)),
    OCI_USER_READ_KEY(CacheKeyPrefix.OCI, "PAR::READ::%s::%s", 2, Duration.ofDays(1)),
    ;

    private final CacheKeyPrefix prefix;
    private final String keyName;
    private final Integer parameterCount;
    private final Duration expire;

    CacheKey(CacheKeyPrefix prefix, String keyName, Integer parameterCount, Duration expire) {
        this.prefix = prefix;
        this.keyName = keyName;
        this.parameterCount = parameterCount;
        this.expire = expire;
    }

    public String generateKey(String... keyParams) {
        if (this.parameterCount == null || this.parameterCount <= 0) {
            return prefix.name() + keyName;
        }
        if (this.parameterCount != keyParams.length) {
            throw new IllegalArgumentException("Invalid number of key parameters");
        }
        return prefix.name() + String.format(keyName, keyParams);
    }

    public Duration expire() {
        return expire;
    }
}
