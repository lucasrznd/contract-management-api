package br.com.lucasrznd.contractmanagementapi.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Provider-agnostic object storage. Domain services only deal with object keys,
 * never with provider specifics (bucket, credentials, URLs).
 */
public interface StorageService {

    /**
     * Uploads the file under the given folder.
     *
     * @return the generated object key (e.g. {@code sellers/1f0c...png}), to be persisted by the caller
     */
    String upload(MultipartFile file, String folder);

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
     * Whether the stored value is an external URL saved before the storage existed, not an object key.
     */
    static boolean isExternalUrl(String key) {
        return key != null && (key.startsWith("http://") || key.startsWith("https://"));
    }

}
