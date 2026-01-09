package com.practice.stomp.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.practice.stomp.config.properties.OciProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class OciConfig {
    private final OciProperties ociProperties;

    @Bean
    public AuthenticationDetailsProvider authenticationDetailsProvider() throws IOException {
        return new ConfigFileAuthenticationDetailsProvider(ConfigFileReader.parseDefault());
    }

    @Bean
    public ObjectStorage objectStorage(AuthenticationDetailsProvider authenticationDetailsProvider) throws IOException {
        return ObjectStorageClient.builder()
                .region(ociProperties.getRegion())
                .build(authenticationDetailsProvider);
    }
}
