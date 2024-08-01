package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static br.com.lucasrznd.contractmanagementapi.creator.CreatorUtils.generateMock;
import static br.com.lucasrznd.contractmanagementapi.utils.CompanyConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService service;

    @Mock
    private ClientCompanyRepository repository;

    @Mock
    private CompanyMapper mapper;

    @Test
    public void saveCompany_WithValidData_ReturnsCompany() {
        when(mapper.toEntity(COMPANY_REQUEST)).thenReturn(COMPANY_ENTITY);
        when(repository.save(COMPANY_ENTITY)).thenReturn(COMPANY_ENTITY);
        when(mapper.toResponse(COMPANY_ENTITY)).thenReturn(COMPANY_RESPONSE);

        var savedCompany = service.save(COMPANY_REQUEST);

        assertThat(savedCompany).isNotNull();
        verify(mapper).toEntity(COMPANY_REQUEST);
        verify(repository).save(any(ClientCompany.class));
    }

    @Test
    public void saveCompany_WithInvalidBusinessName_ThrowsException() {
        var request = COMPANY_REQUEST.withBusinessName("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidTradeName_ThrowsException() {
        var request = COMPANY_REQUEST.withTradeName("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidRegistrationNumber_ThrowsException() {
        var request = COMPANY_REQUEST.withRegistrationNumber("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidPhoneNumber_ThrowsException() {
        var request = COMPANY_REQUEST.withStateRegistration("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidEmail_ThrowsException() {
        var request = COMPANY_REQUEST.withEmail("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidStreetName_ThrowsException() {
        var request = COMPANY_REQUEST.withStreetName("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidAvenueName_ThrowsException() {
        var request = COMPANY_REQUEST.withAvenueName("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidAddressNumber_ThrowsException() {
        var request = COMPANY_REQUEST.withNumber(null);
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidState_ThrowsException() {
        var request = COMPANY_REQUEST.withState("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithInvalidZipCode_ThrowsException() {
        var request = COMPANY_REQUEST.withZipCode("");
        when(mapper.toEntity(request)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, times(0)).save(any());
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void updateCompany_WithExistingId_ReturnsUpdatedCompany() {
        when(repository.findById(1L)).thenReturn(Optional.of(COMPANY_ENTITY));
        when(mapper.update(UPDATE_COMPANY_REQUEST, COMPANY_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(repository.save(UPDATED_ENTITY)).thenReturn(UPDATED_ENTITY);
        when(mapper.toResponse(UPDATED_ENTITY)).thenReturn(COMPANY_RESPONSE);

        var updatedCompany = service.update(1L, UPDATE_COMPANY_REQUEST);

        assertThat(updatedCompany).isNotNull();
        verify(mapper).update(UPDATE_COMPANY_REQUEST, COMPANY_ENTITY);
        verify(repository).save(UPDATED_ENTITY);
    }

    @Test
    public void updateCompany_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, UPDATE_COMPANY_REQUEST)).isInstanceOf(ResourceNotFoundException.class);
    }

}