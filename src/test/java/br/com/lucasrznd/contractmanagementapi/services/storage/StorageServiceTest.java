package br.com.lucasrznd.contractmanagementapi.services.storage;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static br.com.lucasrznd.contractmanagementapi.services.storage.StorageFolder.SELLER_IMAGES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Upload workflow (default methods) of {@link StorageService}, independent of the provider.
 */
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    private static final String KEY = "images/sellers/photo.png";
    private static final MockMultipartFile IMAGE = new MockMultipartFile("image", "photo.png", "image/png", new byte[]{1, 2, 3});

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private StorageService storageService;

    @Test
    public void uploadIfPresent_WithFile_UploadsFile() {
        when(storageService.upload(IMAGE, SELLER_IMAGES)).thenReturn(KEY);

        assertThat(storageService.uploadIfPresent(IMAGE, SELLER_IMAGES)).isEqualTo(KEY);
        verify(storageService).upload(IMAGE, SELLER_IMAGES);
    }

    @Test
    public void uploadIfPresent_WithNullOrEmptyFile_ReturnsNullWithoutUploading() {
        var emptyFile = new MockMultipartFile("image", "photo.png", "image/png", new byte[0]);

        assertThat(storageService.uploadIfPresent(null, SELLER_IMAGES)).isNull();
        assertThat(storageService.uploadIfPresent(emptyFile, SELLER_IMAGES)).isNull();
        verify(storageService, never()).upload(any(), any());
    }

    @Test
    public void discardOnFailure_WithSuccessfulAction_ReturnsResultAndKeepsObject() {
        assertThat(storageService.discardOnFailure(KEY, () -> "saved")).isEqualTo("saved");
        verify(storageService, never()).delete(any());
    }

    @Test
    public void discardOnFailure_WithFailingAction_DeletesObjectAndRethrows() {
        assertThatThrownBy(() -> storageService.discardOnFailure(KEY, () -> {
            throw new IllegalStateException("save failed");
        })).isInstanceOf(IllegalStateException.class).hasMessage("save failed");
        verify(storageService).delete(KEY);
    }

    @Test
    public void discardOnFailure_WithFailingActionAndNoUpload_RethrowsWithoutDeleting() {
        assertThatThrownBy(() -> storageService.discardOnFailure(null, () -> {
            throw new IllegalStateException("save failed");
        })).isInstanceOf(IllegalStateException.class);
        verify(storageService, never()).delete(any());
    }

    @Test
    public void deleteQuietly_WithStorageFailure_DoesNotThrow() {
        doThrow(new StorageException("Error deleting object " + KEY, null)).when(storageService).delete(KEY);

        assertThatCode(() -> storageService.deleteQuietly(KEY)).doesNotThrowAnyException();
        verify(storageService).delete(KEY);
    }

    @Test
    public void deleteQuietly_WithNullKey_DoesNothing() {
        storageService.deleteQuietly(null);

        verify(storageService, never()).delete(any());
    }

}
