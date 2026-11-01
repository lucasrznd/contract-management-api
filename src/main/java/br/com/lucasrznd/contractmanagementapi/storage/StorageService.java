package br.com.lucasrznd.contractmanagementapi.storage;

import org.apache.logging.log4j.LogManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

/**
 * Provider-agnostic object storage. Domain services only deal with object keys,
 * never with provider specifics (bucket, credentials, URLs).
 *
 * <p>Providers implement the primitives ({@link #upload}, {@link #delete}, {@link #getUrl});
 * the default methods build the upload workflow used by the domains on top of them.
 */
public interface StorageService {

    /**
     * Uploads the file under the given folder.
     *
     * @return the generated object key (e.g. {@code images/sellers/1f0c...png}), to be persisted by the caller
     */
    String upload(MultipartFile file, StorageFolder folder);

    /**
     * Deletes the object. Does nothing for blank keys or legacy external URLs.
     */
    void delete(String key);

    /**
     * Returns a URL the client can use to read the object.
     * Blank keys return {@code null}; legacy external URLs (http/https) are returned as is.
     */
    String getUrl(String key);

    /**
     * Uploads the file only when one was sent, for optional file fields.
     *
     * @return the generated object key, or {@code null} when the file is null or empty
     */
    default String uploadIfPresent(MultipartFile file, StorageFolder folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return upload(file, folder);
    }

    /**
     * Runs the action that persists a just uploaded object key (usually a repository save).
     * If it fails, the uploaded object is deleted so no orphan is left in the storage, and the failure is rethrown.
     */
    default <T> T discardOnFailure(String uploadedKey, Supplier<T> action) {
        try {
            return action.get();
        } catch (RuntimeException e) {
            deleteQuietly(uploadedKey);
            throw e;
        }
    }

    /**
     * Deletes the object without ever failing the caller: cleanup errors are only logged,
     * since an orphan object is preferable to a failed request.
     */
    default void deleteQuietly(String key) {
        if (key == null) {
            return;
        }

        try {
            delete(key);
        } catch (RuntimeException e) {
            LogManager.getLogger(StorageService.class).warn("Could not delete object {}: {}", key, e.getMessage());
        }
    }

    /**
     * Whether the stored value is an external URL saved before the storage existed, not an object key.
     */
    static boolean isExternalUrl(String key) {
        return key != null && (key.startsWith("http://") || key.startsWith("https://"));
    }

}
