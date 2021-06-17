package com.elbigs.config;

import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageBlobClientConfig {
    @Value("${blob.account-name}")
    String accountName;

    @Value("${blob.account-key}")
    String accountKey;

    @Value("${blob.default-endpoints-protocol}")
    String defaultEndpointsProtocol;

    @Value("${blob.container-name}")
    String containerName;

    @Bean
    public BlobClientBuilder getClient() {
        BlobClientBuilder client = new BlobClientBuilder();
        client.connectionString("DefaultEndpointsProtocol=" + defaultEndpointsProtocol + ";AccountName=" + accountName + ";AccountKey=" + accountKey);
        client.containerName(containerName);
        return client;
    }
}
