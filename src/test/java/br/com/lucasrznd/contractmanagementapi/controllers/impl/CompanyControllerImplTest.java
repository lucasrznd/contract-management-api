package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.services.CompanyService;
import br.com.lucasrznd.contractmanagementapi.storage.StorageException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.RecordComponent;
import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.CompanyConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyControllerImpl.class)
class CompanyControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService service;

    @Test
    public void saveCompany_WithValidData_ReturnsCreated() throws Exception {
        when(service.save(COMPANY_REQUEST)).thenReturn(COMPANY_RESPONSE);

        mockMvc.perform(withFields(multipart("/companies"), COMPANY_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessName").value(COMPANY_RESPONSE.businessName()))
                .andExpect(jsonPath("$.urlImage").value(COMPANY_RESPONSE.urlImage()));
    }

    @Test
    public void saveCompany_WithImage_PassesImageToService() throws Exception {
        var captor = ArgumentCaptor.forClass(CompanyRequest.class);
        when(service.save(any(CompanyRequest.class))).thenReturn(COMPANY_RESPONSE);

        mockMvc.perform(withFields(multipart("/companies").file(COMPANY_IMAGE), COMPANY_REQUEST))
                .andExpect(status().isCreated());

        verify(service).save(captor.capture());
        assertThat(captor.getValue().businessName()).isEqualTo(COMPANY_REQUEST.businessName());
        assertThat(captor.getValue().image().getOriginalFilename()).isEqualTo(COMPANY_IMAGE.getOriginalFilename());
        assertThat(captor.getValue().image().getBytes()).isEqualTo(COMPANY_IMAGE.getBytes());
    }

    @Test
    public void saveCompany_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(withFields(multipart("/companies"), INVALID_COMPANY_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    public void saveCompany_WithNonImageFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(withFields(multipart("/companies").file(INVALID_COMPANY_IMAGE), COMPANY_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("image"));

        verifyNoInteractions(service);
    }

    @Test
    public void saveCompany_WithStorageFailure_ReturnsInternalServerError() throws Exception {
        when(service.save(any(CompanyRequest.class))).thenThrow(new StorageException("Error uploading file logo.png", null));

        mockMvc.perform(withFields(multipart("/companies").file(COMPANY_IMAGE), COMPANY_REQUEST))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void findCompanyById_WithExistingId_ReturnsOk() throws Exception {
        when(service.findById(1L)).thenReturn(COMPANY_RESPONSE);

        mockMvc.perform(get("/companies/" + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value(COMPANY_RESPONSE.businessName()));
    }

    @Test
    public void findCompanyById_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).findById(1L);

        mockMvc.perform(get("/companies/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void updateCompany_WithExistingId_ReturnsOk() throws Exception {
        when(service.update(1L, UPDATE_COMPANY_REQUEST)).thenReturn(COMPANY_RESPONSE);

        mockMvc.perform(withFields(multipart(PUT, "/companies/" + 1), UPDATE_COMPANY_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value(COMPANY_RESPONSE.businessName()));
    }

    @Test
    public void updateCompany_WithImage_PassesImageToService() throws Exception {
        var captor = ArgumentCaptor.forClass(UpdateCompanyRequest.class);
        when(service.update(eq(1L), any(UpdateCompanyRequest.class))).thenReturn(COMPANY_RESPONSE);

        mockMvc.perform(withFields(multipart(PUT, "/companies/" + 1).file(COMPANY_IMAGE), UPDATE_COMPANY_REQUEST))
                .andExpect(status().isOk());

        verify(service).update(eq(1L), captor.capture());
        assertThat(captor.getValue().image().getBytes()).isEqualTo(COMPANY_IMAGE.getBytes());
    }

    @Test
    public void updateCompany_WithNonImageFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(withFields(multipart(PUT, "/companies/" + 1).file(INVALID_COMPANY_IMAGE), UPDATE_COMPANY_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("image"));

        verifyNoInteractions(service);
    }

    @Test
    public void updateCompany_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).update(1L, UPDATE_COMPANY_REQUEST);

        mockMvc.perform(withFields(multipart(PUT, "/companies/" + 1), UPDATE_COMPANY_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void listCompanies_ReturnsPagedCompanies() throws Exception {
        var pageable = PageRequest.of(0, 10);
        when(service.findAll(isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(COMPANY_RESPONSE), pageable, 1));

        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10));
    }

    @Test
    public void listCompanies_WithPageParams_PassesPageableToService() throws Exception {
        when(service.findAll(isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/companies").param("page", "2").param("size", "5").param("sort", "id,desc"))
                .andExpect(status().isOk());

        verify(service).findAll(null, PageRequest.of(2, 5, Sort.by("id").descending()));
    }

    @Test
    public void listCompanies_WithText_PassesTextToService() throws Exception {
        when(service.findAll(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(COMPANY_RESPONSE)));

        mockMvc.perform(get("/companies").param("text", "joão"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(service).findAll("joão", PageRequest.of(0, 10, Sort.by("id")));
    }

    @Test
    public void countCompanies_ReturnsOk() throws Exception {
        when(service.countCompanies()).thenReturn(1);

        mockMvc.perform(get("/companies/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    public void deleteCompany_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/companies/" + 1))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void deleteCompany_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).delete(1L);

        mockMvc.perform(delete("/companies/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    /**
     * Sends every non-null text field of the request record as a multipart form field (the image goes as a file part).
     */
    private static MockMultipartHttpServletRequestBuilder withFields(final MockMultipartHttpServletRequestBuilder builder,
                                                                     final Record request) throws Exception {
        for (RecordComponent component : request.getClass().getRecordComponents()) {
            Object value = component.getAccessor().invoke(request);
            if (value != null && !(value instanceof MultipartFile)) {
                builder.param(component.getName(), value.toString());
            }
        }
        return builder;
    }

}