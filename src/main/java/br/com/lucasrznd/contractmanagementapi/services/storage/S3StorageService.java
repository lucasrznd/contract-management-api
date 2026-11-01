package br.com.lucasrznd.contractmanagementapi.services.storage;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StorageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * {@link StorageService} for any S3-compatible provider (AWS S3, SeaweedFS, RustFS, Garage, Cloudflare R2...).
 * Objects are private; reads go through presigned URLs.
 */
@Service
public class S3StorageService implements StorageService {

    private static final Logger logger = LogManager.getLogger();

    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "application/pdf", ".pdf");

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final StorageProperties properties;

    public S3StorageService(S3Client s3Client, S3Presigner presigner, StorageProperties properties) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.properties = properties;
    }

    @Override
    public String upload(MultipartFile file, StorageFolder folder) {
        String key = folder.prefix() + "/" + UUID.randomUUID() + extension(file);

        try (InputStream content = file.getInputStream()) {
            s3Client.putObject(request -> request.bucket(properties.bucket()).key(key).contentType(file.getContentType()),
                    RequestBody.fromInputStream(content, file.getSize()));
            return key;
        } catch (IOException | SdkException e) {
            throw new StorageException("Error uploading file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String key) {
        if (StringUtils.isBlank(key) || StorageService.isExternalUrl(key)) {
            return;
        }

        try {
            s3Client.deleteObject(request -> request.bucket(properties.bucket()).key(key));
        } catch (SdkException e) {
            throw new StorageException("Error deleting object " + key, e);
        }
    }

    @Override
    public String getUrl(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (StorageService.isExternalUrl(key)) {
            return key;
        }

        return presigner.presignGetObject(request -> request
                        .signatureDuration(properties.urlExpiration())
                        .getObjectRequest(get -> get.bucket(properties.bucket()).key(key)))
                .url().toString();
    }

    /**
     * Creates the bucket on startup when enabled (dev environments). Never prevents the application from starting.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createBucketIfMissing() {
        if (!properties.autoCreateBucket()) {
            return;
        }

        try {
            s3Client.headBucket(request -> request.bucket(properties.bucket()));
        } catch (NoSuchBucketException e) {
            createBucket();
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                createBucket();
            } else {
                logger.warn("Could not check bucket {}: {}", properties.bucket(), e.getMessage());
            }
        } catch (SdkException e) {
            logger.warn("Could not reach object storage to check bucket {}: {}", properties.bucket(), e.getMessage());
        }
    }

    private void createBucket() {
        try {
            s3Client.createBucket(request -> request.bucket(properties.bucket()));
            logger.info("Bucket {} created", properties.bucket());
        } catch (SdkException e) {
            logger.warn("Could not create bucket {}: {}", properties.bucket(), e.getMessage());
        }
    }

    private static String extension(MultipartFile file) {
        String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        if (StringUtils.isNotBlank(extension)) {
            return "." + extension.toLowerCase();
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return "";
        }
        MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
        return EXTENSIONS.getOrDefault(mimeType.getType() + "/" + mimeType.getSubtype(), "");
    }

}
