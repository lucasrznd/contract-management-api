package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import br.com.lucasrznd.contractmanagementapi.mappers.ContractMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static br.com.lucasrznd.contractmanagementapi.common.ContractConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @InjectMocks
    private ContractService service;

    @Mock
    private ContractRepository repository;

    @Mock
    private ContractMapper mapper;

    @Test
    public void saveContract_WithValidData_ReturnsContract() {
        when(mapper.toEntity(CONTRACT_REQUEST)).thenReturn(CONTRACT_ENTITY);
        when(repository.save(CONTRACT_ENTITY)).thenReturn(CONTRACT_ENTITY);
        when(mapper.toResponse(CONTRACT_ENTITY)).thenReturn(CONTRACT_RESPONSE);

        var savedContract = service.save(CONTRACT_REQUEST);

        assertThat(savedContract).isNotNull();
        verify(mapper).toEntity(CONTRACT_REQUEST);
        verify(repository).save(CONTRACT_ENTITY);
        verify(mapper).toResponse(CONTRACT_ENTITY);
    }

    @Test
    public void saveContract_WithInvalidClientCompany_ThrowsException() {
        var request = CONTRACT_REQUEST.withClientCompany(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidAdvertisingOrder_ThrowsException() {
        var request = CONTRACT_REQUEST.withAdvertisingOrder(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidSpotDuration_ThrowsException() {
        var request = CONTRACT_REQUEST.withSpotDuration(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidStartDate_ThrowsException() {
        var request = CONTRACT_REQUEST.withStartDate(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidEndDate_ThrowsException() {
        var request = CONTRACT_REQUEST.withEndDate(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidMonthlyPrice_ThrowsException() {
        var request = CONTRACT_REQUEST.withMonthlyPrice(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidPaymentMethod_ThrowsException() {
        var request = CONTRACT_REQUEST.withPaymentMethod(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveContract_WithInvalidPaymentDueDay_ThrowsException() {
        var request = CONTRACT_REQUEST.withPaymentDueDay(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void updateContract_WithExistingId_ReturnsUpdatedContract() {
        when(repository.findById(1L)).thenReturn(Optional.of(CONTRACT_ENTITY));
        when(mapper.update(UPDATE_CONTRACT_REQUEST, CONTRACT_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(repository.save(UPDATED_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(mapper.toResponse(UPDATED_ENTITY)).thenReturn(CONTRACT_RESPONSE);

        var updatedContract = service.update(1L, UPDATE_CONTRACT_REQUEST);

        assertThat(updatedContract).isNotNull();
        verify(mapper).update(UPDATE_CONTRACT_REQUEST, CONTRACT_ENTITY);
        verify(repository).save(UPDATED_ENTITY);
    }

    @Test
    public void updateContract_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, UPDATE_CONTRACT_REQUEST)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void listContracts_ReturnsAllContracts() {
        List<Contract> list = List.of(CONTRACT_ENTITY);
        when(repository.findAll()).thenReturn(list);
        when(mapper.toResponse(CONTRACT_ENTITY)).thenReturn(CONTRACT_RESPONSE);

        var contractsList = service.findAll();

        assertThat(contractsList).isNotNull();
        assertThat(contractsList.size()).isEqualTo(1);
        verify(repository).findAll();
        verify(mapper).toResponse(CONTRACT_ENTITY);
    }

    @Test
    public void deleteContract_WithExistingId_DoesNotThrowAnyException() {
        when(repository.findById(1L)).thenReturn(Optional.of(CONTRACT_ENTITY));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).findById(1L);
        verify(repository).delete(any());
    }

    @Test
    public void deleteContract_WithUnexistingId_ThrowsException() {
        assertThatCode(() -> service.delete(1L)).isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findById(1L);
        verify(repository, times(0)).delete(any());
    }

}