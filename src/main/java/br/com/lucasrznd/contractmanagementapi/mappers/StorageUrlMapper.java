package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * Converts stored object keys into URLs readable by the client. Reusable by any mapper ({@code uses = StorageUrlMapper.class}).
 */
@Component
@RequiredArgsConstructor
public class StorageUrlMapper {

    private final StorageService storageService;

    @Named("toImageUrl")
    public String toImageUrl(final String key) {
        return storageService.getUrl(key);
    }

}
