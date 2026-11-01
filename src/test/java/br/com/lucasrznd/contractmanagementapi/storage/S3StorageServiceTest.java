package br.com.lucasrznd.contractmanagementapi.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs {@link S3StorageService} against a real S3-compatible server (SeaweedFS, same as docker-compose).
 * Skipped automatically when Docker is not available.
 */
@Testcontainers(disabledWithoutDocker = true)
class S3StorageServiceTest {

    private static final String ACCESS_KEY = "test-access-key";
    private static final String SECRET_KEY = "test-secret-key";
    private static final String BUCKET = "test-bucket";
    private static final int S3_PORT = 8333;

    private static final String S3_CONFIG = """
            {"identities": [{"name": "test",
              "credentials": [{"accessKey": "%s", "secretKey": "%s"}],
              "actions": ["Admin", "Read", "Write", "List", "Tagging"]}]}
            """.formatted(ACCESS_KEY, SECRET_KEY);

    @Container
    static GenericContainer<?> seaweedfs = new GenericContainer<>("chrislusf/seaweedfs:4.51")
            .withCommand("server", "-dir=/data", "-s3", "-s3.config=/etc/seaweedfs/s3.json")
            .withCopyToContainer(Transferable.of(S3_CONFIG), "/etc/seaweedfs/s3.json")
            .withExposedPorts(S3_PORT)
            .waitingFor(Wait.forListeningPorts(S3_PORT))
            .withStartupTimeout(Duration.ofMinutes(2));

    private static S3Client s3Client;
    private static S3StorageService service;

    @BeforeAll
    static void setUp() {
        var properties = new StorageProperties(BUCKET, "us-east-1",
                "http://" + seaweedfs.getHost() + ":" + seaweedfs.getMappedPort(S3_PORT), null,
                ACCESS_KEY, SECRET_KEY, true, Duration.ofMinutes(5), true);

        var config = new S3StorageConfig();
        s3Client = config.s3Client(properties);
        service = new S3StorageService(s3Client, config.s3Presigner(properties), properties);
        service.createBucketIfMissing();
    }

    @Test
    public void upload_WithFile_StoresObjectReadableByPresignedUrl() throws Exception {
        var content = "fake-png-content".getBytes();
        var file = new MockMultipartFile("image", "Photo.PNG", "image/png", content);

        var key = service.upload(file, "sellers");

        assertThat(key).startsWith("sellers/").endsWith(".png");
        var response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(service.getUrl(key))).build(), HttpResponse.BodyHandlers.ofByteArray());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(content);
        assertThat(response.headers().firstValue("Content-Type")).hasValue("image/png");
    }

    @Test
    public void delete_WithExistingKey_RemovesObject() {
        var key = service.upload(new MockMultipartFile("image", "photo.png", "image/png", new byte[]{1, 2, 3}), "sellers");

        service.delete(key);

        assertThatThrownBy(() -> s3Client.headObject(request -> request.bucket(BUCKET).key(key)))
                .isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    public void getUrl_WithExternalUrl_ReturnsUrlAsIs() {
        assertThat(service.getUrl("https://cdn.example.com/photo.png")).isEqualTo("https://cdn.example.com/photo.png");
    }

    @Test
    public void getUrl_WithBlankKey_ReturnsNull() {
        assertThat(service.getUrl(null)).isNull();
        assertThat(service.getUrl("")).isNull();
    }

}
