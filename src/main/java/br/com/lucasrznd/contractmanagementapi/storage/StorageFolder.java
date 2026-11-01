package br.com.lucasrznd.contractmanagementapi.storage;

/**
 * Folders (key prefixes) inside the bucket. Every domain stores its files under its own folder,
 * grouped by file kind (e.g. {@code images/sellers}), so a single bucket keeps them separated.
 */
public enum StorageFolder {

    SELLER_IMAGES("images/sellers"),
    COMPANY_IMAGES("images/companies");

    private final String prefix;

    StorageFolder(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

}
