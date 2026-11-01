package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
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

import static br.com.lucasrznd.contractmanagementapi.common.CompanyConstants.*;
import static br.com.lucasrznd.contractmanagementapi.storage.StorageFolder.COMPANY_IMAGES;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private static final String NEW_IMAGE_KEY = "images/companies/new-image.png";

    @InjectMocks
    private CompanyService service;

    @Mock
    private ClientCompanyRepository repository;

    @Mock
    private CompanyMapper mapper;

    /**
     * Real default methods (upload workflow) on top of the mocked provider primitives.
     */
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private StorageService storageService;

    @Test
    public void saveCompany_WithValidData_ReturnsCompany() {
        var company = newCompany(null);
        when(mapper.toEntity(COMPANY_REQUEST)).thenReturn(company);
        when(repository.save(company)).thenReturn(company);
        when(mapper.toResponse(company)).thenReturn(COMPANY_RESPONSE);

        var savedCompany = service.save(COMPANY_REQUEST);

        assertThat(savedCompany).isNotNull();
        assertThat(company.getImageKey()).isNull();
        verify(mapper).toEntity(COMPANY_REQUEST);
        verify(repository).save(any(ClientCompany.class));
        verifyNoStorageChanges();
    }

    @Test
    public void saveCompany_WithImage_UploadsImageAndSavesKey() {
        var request = COMPANY_REQUEST.withImage(COMPANY_IMAGE);
        var company = newCompany(null);
        when(mapper.toEntity(request)).thenReturn(company);
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenReturn(COMPANY_IMAGE_KEY);
        when(repository.save(company)).thenReturn(company);
        when(mapper.toResponse(company)).thenReturn(COMPANY_RESPONSE);

        var savedCompany = service.save(request);

        assertThat(savedCompany).isEqualTo(COMPANY_RESPONSE);
        assertThat(company.getImageKey()).isEqualTo(COMPANY_IMAGE_KEY);
        verify(storageService).upload(COMPANY_IMAGE, COMPANY_IMAGES);
    }

    @Test
    public void saveCompany_WithImageAndRepositoryFailure_DeletesUploadedImage() {
        var request = COMPANY_REQUEST.withImage(COMPANY_IMAGE);
        var company = newCompany(null);
        when(mapper.toEntity(request)).thenReturn(company);
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenReturn(COMPANY_IMAGE_KEY);
        when(repository.save(company)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(DataIntegrityViolationException.class);
        verify(storageService).delete(COMPANY_IMAGE_KEY);
        verify(mapper, times(0)).toResponse(any());
    }

    @Test
    public void saveCompany_WithStorageFailure_DoesNotSaveCompany() {
        var request = COMPANY_REQUEST.withImage(COMPANY_IMAGE);
        when(mapper.toEntity(request)).thenReturn(newCompany(null));
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenThrow(StorageException.class);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(StorageException.class);
        verify(repository, times(0)).save(any());
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
    public void findCompanyById_WithExistingId_ReturnsCompany() {
        when(repository.findById(1L)).thenReturn(Optional.of(COMPANY_ENTITY));
        when(mapper.toResponse(COMPANY_ENTITY)).thenReturn(COMPANY_RESPONSE);

        var foundCompany = service.findById(1L);

        assertThat(foundCompany).isEqualTo(COMPANY_RESPONSE);
        verify(repository).findById(1L);
        verify(mapper).toResponse(COMPANY_ENTITY);
    }

    @Test
    public void findCompanyById_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(ResourceNotFoundException.class);
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
        assertThat(UPDATED_ENTITY.getImageKey()).isEqualTo(COMPANY_IMAGE_KEY);
        verify(mapper).update(UPDATE_COMPANY_REQUEST, COMPANY_ENTITY);
        verify(repository).save(UPDATED_ENTITY);
        verifyNoStorageChanges();
    }

    @Test
    public void updateCompany_WithNewImage_ReplacesImageAndDeletesOldOne() {
        var company = newCompany(COMPANY_IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(company));
        when(mapper.update(UPDATE_COMPANY_REQUEST_WITH_IMAGE, company)).thenReturn(company);
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(company)).thenReturn(company);
        when(mapper.toResponse(company)).thenReturn(COMPANY_RESPONSE);

        service.update(1L, UPDATE_COMPANY_REQUEST_WITH_IMAGE);

        assertThat(company.getImageKey()).isEqualTo(NEW_IMAGE_KEY);
        verify(storageService).delete(COMPANY_IMAGE_KEY);
        verify(storageService, times(0)).delete(NEW_IMAGE_KEY);
    }

    @Test
    public void updateCompany_WithNewImageAndDeleteFailure_StillReturnsUpdatedCompany() {
        var company = newCompany(COMPANY_IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(company));
        when(mapper.update(UPDATE_COMPANY_REQUEST_WITH_IMAGE, company)).thenReturn(company);
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(company)).thenReturn(company);
        when(mapper.toResponse(company)).thenReturn(COMPANY_RESPONSE);
        doThrow(StorageException.class).when(storageService).delete(COMPANY_IMAGE_KEY);

        assertThat(service.update(1L, UPDATE_COMPANY_REQUEST_WITH_IMAGE)).isEqualTo(COMPANY_RESPONSE);
    }

    @Test
    public void updateCompany_WithNewImageAndRepositoryFailure_KeepsOldImage() {
        var company = newCompany(COMPANY_IMAGE_KEY);
        when(repository.findById(1L)).thenReturn(Optional.of(company));
        when(mapper.update(UPDATE_COMPANY_REQUEST_WITH_IMAGE, company)).thenReturn(company);
        when(storageService.upload(COMPANY_IMAGE, COMPANY_IMAGES)).thenReturn(NEW_IMAGE_KEY);
        when(repository.save(company)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.update(1L, UPDATE_COMPANY_REQUEST_WITH_IMAGE)).isInstanceOf(DataIntegrityViolationException.class);
        verify(storageService).delete(NEW_IMAGE_KEY);
        verify(storageService, times(0)).delete(COMPANY_IMAGE_KEY);
    }

    @Test
    public void updateCompany_WithUnexistingId_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, UPDATE_COMPANY_REQUEST)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void listCompanies_ReturnsPagedCompanies() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(COMPANY_ENTITY), pageable, 1));
        when(mapper.toResponse(COMPANY_ENTITY)).thenReturn(COMPANY_RESPONSE);

        var companiesPage = service.findAll("text", pageable);

        assertThat(companiesPage).isNotNull();
        assertThat(companiesPage.getContent()).containsExactly(COMPANY_RESPONSE);
        assertThat(companiesPage.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toResponse(COMPANY_ENTITY);
    }

    @Test
    public void countCompanies_ReturnsCompaniesQuantity() {
        when(repository.count()).thenReturn(1L);

        var companiesQuantity = service.countCompanies();

        assertThat(companiesQuantity).isNotNull();
        assertThat(companiesQuantity).isEqualTo(1);
        verify(repository).count();
    }

    @Test
    public void deleteCompany_WithExistingId_DoesNotThrowAnyException() {
        when(repository.findById(1L)).thenReturn(Optional.of(COMPANY_ENTITY));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
        verify(repository).findById(1L);
        verify(repository).delete(any(ClientCompany.class));
        verify(storageService).delete(COMPANY_IMAGE_KEY);
    }

    @Test
    public void deleteCompany_WithRepositoryFailure_KeepsImage() {
        when(repository.findById(1L)).thenReturn(Optional.of(COMPANY_ENTITY));
        doThrow(DataIntegrityViolationException.class).when(repository).delete(COMPANY_ENTITY);

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(DataIntegrityViolationException.class);
        verifyNoStorageChanges();
    }

    @Test
    public void deleteCompany_WithUnexistingId_ThrowsException() {
        assertThatCode(() -> service.delete(1L)).isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findById(1L);
        verify(repository, times(0)).delete(any(ClientCompany.class));
        verifyNoStorageChanges();
    }

    /**
     * Fresh instance per test: the service mutates the company (image key), so shared constants must not be used.
     */
    private static ClientCompany newCompany(final String imageKey) {
        var c = COMPANY_ENTITY;
        return new ClientCompany(c.getId(), c.getBusinessName(), c.getTradeName(), c.getRegistrationNumber(), c.getCpf(),
                c.getStateRegistration(), c.getPhoneNumber(), c.getEmail(), c.getStreetName(), c.getAvenueName(),
                c.getNumber(), c.getCity(), c.getState(), c.getZipCode(), imageKey);
    }

    private void verifyNoStorageChanges() {
        verify(storageService, never()).upload(any(), any());
        verify(storageService, never()).delete(any());
    }

}
