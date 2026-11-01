package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.mappers.SellerMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.SellerRepository;
import br.com.lucasrznd.contractmanagementapi.storage.StorageException;
import br.com.lucasrznd.contractmanagementapi.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.*;
import static br.com.lucasrznd.contractmanagementapi.storage.StorageFolder.SELLER_IMAGES;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    private static final String NEW_IMAGE_KEY = "images/sellers/new-image.png";

    @InjectMocks
    private SellerService service;

    @Mock
    private SellerRepository repository;

    @Mock
    private SellerMapper mapper;

    /**
     * Real default methods (upload workflow) on top of the mocked provider primitives.
     */
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private StorageService storageService;

    @Test
    public void saveSeller_WithValidData_ReturnsSeller() {
        var seller = newSeller(null);
        when(mapper.toEntity(SELLER_REQUEST)).thenReturn(seller);
        when(repository.save(seller)).thenReturn(seller);
        when(mapper.toResponse(seller)).thenReturn(SELLER_RESPONSE);

        var savedSeller = service.save(SELLER_REQUEST);

        assertThat(savedSeller).isNotNull();
        assertThat(seller.getImageKey()).isNull();
        verify(mapper).toEntity(SELLER_REQUEST);
        verify(repository).save(any(Seller.class));
        verifyNoStorageChanges();
    }

    @Test
    public void saveSeller_WithImage_UploadsImageAndSavesKey() {
        var request = SELLER_REQUEST.withImage(SELLER_IMAGE);
        var seller = newSeller(null);
        when(mapper.toEntity(request)).thenReturn(seller);
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenReturn(IMAGE_KEY);
        when(repository.save(seller)).thenReturn(seller);
        when(mapper.toResponse(seller)).thenReturn(SELLER_RESPONSE);

        var savedSeller = service.save(request);

        assertThat(savedSeller).isEqualTo(SELLER_RESPONSE);
        assertThat(seller.getImageKey()).isEqualTo(IMAGE_KEY);
        verify(storageService).upload(SELLER_IMAGE, SELLER_IMAGES);
    }

    @Test
    public void saveSeller_WithImageAndRepositoryFailure_DeletesUploadedImage() {
        var request = SELLER_REQUEST.withImage(SELLER_IMAGE);
        var seller = newSeller(null);
        when(mapper.toEntity(request)).thenReturn(seller);
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenReturn(IMAGE_KEY);
        when(repository.save(seller)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(storageService).delete(IMAGE_KEY);
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveSeller_WithStorageFailure_DoesNotSaveSeller() {
        var request = SELLER_REQUEST.withImage(SELLER_IMAGE);
        when(mapper.toEntity(request)).thenReturn(newSeller(null));
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenThrow(StorageException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(StorageException.class);
        verify(repository, times(0)).save(any());
    }

    @Test
    public void saveSeller_WithInvalidName_ThrowsException() {
        var request = SELLER_REQUEST.withName("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveSeller_WithInvalidPhoneNumber_ThrowsException() {
        var request = SELLER_REQUEST.withPhoneNumber("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void findSellerById_WithExistingId_ReturnsSeller() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));
        when(mapper.toResponse(SELLER_ENTITY)).thenReturn(SELLER_RESPONSE);

        var foundSeller = service.findById(1L);

        assertThat(foundSeller).isEqualTo(SELLER_RESPONSE);
        verify(repository).findById(1L);
        verify(mapper).toResponse(SELLER_ENTITY);
    }

    @Test
    public void findSellerById_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(ResourceNotFoundException.class);
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void updateSeller_WithExistingId_ReturnsUpdatedSeller() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));
        when(mapper.update(UPDATE_SELLER_REQUEST, SELLER_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(repository.save(UPDATED_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(mapper.toResponse(UPDATED_ENTITY)).thenReturn(SELLER_RESPONSE);

        var updatedSeller = service.update(1L, UPDATE_SELLER_REQUEST);

        assertThat(updatedSeller).isNotNull();
        assertThat(UPDATED_ENTITY.getImageKey()).isEqualTo(IMAGE_KEY);
        verify(mapper).update(UPDATE_SELLER_REQUEST, SELLER_ENTITY);
        verify(repository).save(UPDATED_ENTITY);
        verifyNoStorageChanges();
    }

    @Test
    public void updateSeller_WithNewImage_ReplacesImageAndDeletesOldOne() {
        var request = UPDATE_SELLER_REQUEST_WITH_IMAGE;
        var seller = newSeller(IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(seller));
        when(mapper.update(request, seller)).thenReturn(seller);
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(seller)).thenReturn(seller);
        when(mapper.toResponse(seller)).thenReturn(SELLER_RESPONSE);

        service.update(1L, request);

        assertThat(seller.getImageKey()).isEqualTo(NEW_IMAGE_KEY);
        verify(storageService).delete(IMAGE_KEY);
        verify(storageService, times(0)).delete(NEW_IMAGE_KEY);
    }

    @Test
    public void updateSeller_WithNewImageAndDeleteFailure_StillReturnsUpdatedSeller() {
        var request = UPDATE_SELLER_REQUEST_WITH_IMAGE;
        var seller = newSeller(IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(seller));
        when(mapper.update(request, seller)).thenReturn(seller);
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(seller)).thenReturn(seller);
        when(mapper.toResponse(seller)).thenReturn(SELLER_RESPONSE);
        doThrow(StorageException.class).when(storageService).delete(IMAGE_KEY);

        assertThat(service.update(1L, request)).isEqualTo(SELLER_RESPONSE);
    }

    @Test
    public void updateSeller_WithNewImageAndRepositoryFailure_KeepsOldImage() {
        var request = UPDATE_SELLER_REQUEST_WITH_IMAGE;
        var seller = newSeller(IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(seller));
        when(mapper.update(request, seller)).thenReturn(seller);
        when(storageService.upload(SELLER_IMAGE, SELLER_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(seller)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.update(1L, request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(storageService).delete(NEW_IMAGE_KEY);
        verify(storageService, times(0)).delete(IMAGE_KEY);
    }

    @Test
    public void updateSeller_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, UPDATE_SELLER_REQUEST)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoStorageChanges();
    }

    @Test
    public void listSellers_ReturnsPagedSellers() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(SELLER_ENTITY), pageable, 1));
        when(mapper.toResponse(SELLER_ENTITY)).thenReturn(SELLER_RESPONSE);

        var sellersPage = service.findAll("lucas", pageable);

        assertThat(sellersPage).isNotNull();
        assertThat(sellersPage.getContent()).containsExactly(SELLER_RESPONSE);
        assertThat(sellersPage.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toResponse(any(Seller.class));
    }

    @Test
    public void deleteSeller_WithExistingId_DoesNotThrowAnyException() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).findById(1L);
        verify(repository).delete(any(Seller.class));
        verify(storageService).delete(IMAGE_KEY);
    }

    @Test
    public void deleteSeller_WithRepositoryFailure_KeepsImage() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));
        doThrow(DataIntegrityViolationException.class).when(repository).delete(SELLER_ENTITY);

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(DataIntegrityViolationException.class);
        verifyNoStorageChanges();
    }

    @Test
    public void deleteSeller_WithUnexistingId_ThrowsException() {
        assertThatCode(() -> service.delete(1L)).isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findById(1L);
        verify(repository, times(0)).delete(any(Seller.class));
        verifyNoStorageChanges();
    }

    /**
     * Fresh instance per test: the service mutates the seller (image key), so shared constants must not be used.
     */
    private static Seller newSeller(final String imageKey) {
        return new Seller(SELLER_ENTITY.getId(), SELLER_ENTITY.getName(), SELLER_ENTITY.getPhoneNumber(), imageKey);
    }

    private void verifyNoStorageChanges() {
        verify(storageService, never()).upload(any(), any());
        verify(storageService, never()).delete(any());
    }

}
