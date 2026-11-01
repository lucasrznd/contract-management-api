package br.com.lucasrznd.contractmanagementapi.storage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class S3StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties properties) {
        var builder = S3Client.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider(properties))
                .forcePathStyle(properties.pathStyleAccess());

        if (StringUtils.isNotBlank(properties.endpoint())) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(StorageProperties properties) {
        var builder = S3Presigner.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider(properties))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(properties.pathStyleAccess()).build());

        // Presigned URLs are opened by the browser, so they may need a different host than the API uses internally
        String endpoint = StringUtils.firstNonBlank(properties.publicEndpoint(), properties.endpoint());
        if (endpoint != null) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    private static AwsCredentialsProvider credentialsProvider(StorageProperties properties) {
        if (StringUtils.isAnyBlank(properties.accessKey(), properties.secretKey())) {
            return DefaultCredentialsProvider.builder().build();
        }
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
    }

}
