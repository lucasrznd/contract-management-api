package br.com.lucasrznd.contractmanagementapi.services.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Object storage settings ({@code storage.*}). Blank {@code endpoint} means AWS S3 itself;
 * blank keys mean the AWS default credentials chain.
 */
@ConfigurationProperties("storage")
public record StorageProperties(
        String bucket,
        @DefaultValue("us-east-1") String region,
        String endpoint,
        String publicEndpoint,
        String accessKey,
        String secretKey,
        boolean pathStyleAccess,
        @DefaultValue("1h") Duration urlExpiration,
        boolean autoCreateBucket) {
}
