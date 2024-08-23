package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.mappers.SellerMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @InjectMocks
    private SellerService service;

    @Mock
    private SellerRepository repository;

    @Mock
    private SellerMapper mapper;

    @Test
    public void saveSeller_WithValidData_ReturnsSeller() {
        when(mapper.toEntity(SELLER_REQUEST)).thenReturn(SELLER_ENTITY);
        when(repository.save(SELLER_ENTITY)).thenReturn(SELLER_ENTITY);
        when(mapper.toResponse(SELLER_ENTITY)).thenReturn(SELLER_RESPONSE);

        var savedSeller = service.save(SELLER_REQUEST);

        assertThat(savedSeller).isNotNull();
        verify(mapper).toEntity(SELLER_REQUEST);
        verify(repository).save(any(Seller.class));
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
    public void updateSeller_WithExistingId_ReturnsUpdatedSeller() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));
        when(mapper.update(UPDATE_SELLER_REQUEST, SELLER_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(repository.save(UPDATED_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(mapper.toResponse(UPDATED_ENTITY)).thenReturn(SELLER_RESPONSE);

        var updatedSeller = service.update(1L, UPDATE_SELLER_REQUEST);

        assertThat(updatedSeller).isNotNull();
        verify(mapper).update(UPDATE_SELLER_REQUEST, SELLER_ENTITY);
        verify(repository).save(UPDATED_ENTITY);
    }

    @Test
    public void updateSeller_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, UPDATE_SELLER_REQUEST)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void listSellers_ReturnsAllSellers() {
        List<Seller> list = List.of(SELLER_ENTITY);
        when(repository.findAll()).thenReturn(list);
        when(mapper.toResponse(list.get(0))).thenReturn(SELLER_RESPONSE);

        var sellersList = service.findAll();

        assertThat(sellersList).isNotNull();
        assertThat(sellersList.size()).isEqualTo(1);
        verify(repository).findAll();
        verify(mapper).toResponse(any(Seller.class));
    }

    @Test
    public void deleteSeller_WithExistingId_DoesNotThrowAnyException() {
        when(repository.findById(1L)).thenReturn(Optional.of(SELLER_ENTITY));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).findById(1L);
        verify(repository).delete(any());
    }

    @Test
    public void deleteSeller_WithUnexistingId_ThrowsException() {
        assertThatCode(() -> service.delete(1L)).isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findById(1L);
        verify(repository, times(0)).delete(any());
    }

}