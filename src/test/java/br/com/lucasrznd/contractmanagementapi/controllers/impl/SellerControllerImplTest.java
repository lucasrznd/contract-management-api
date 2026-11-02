package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.config.WebConfig;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.StorageException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.services.SellerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.*;
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

@WebMvcTest(controllers = SellerControllerImpl.class)
class SellerControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerService service;

    @MockitoBean
    private WebConfig webConfig;

    @Test
    public void saveSeller_WithValidData_ReturnsCreated() throws Exception {
        when(service.save(SELLER_REQUEST)).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(multipart("/sellers")
                        .param("name", SELLER_REQUEST.name())
                        .param("phoneNumber", SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(SELLER_RESPONSE.name()))
                .andExpect(jsonPath("$.urlImage").value(SELLER_RESPONSE.urlImage()));
    }

    @Test
    public void saveSeller_WithImage_PassesImageToService() throws Exception {
        var captor = ArgumentCaptor.forClass(SellerRequest.class);
        when(service.save(any(SellerRequest.class))).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(multipart("/sellers")
                        .file(SELLER_IMAGE)
                        .param("name", SELLER_REQUEST.name())
                        .param("phoneNumber", SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isCreated());

        verify(service).save(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo(SELLER_REQUEST.name());
        assertThat(captor.getValue().image().getOriginalFilename()).isEqualTo(SELLER_IMAGE.getOriginalFilename());
        assertThat(captor.getValue().image().getBytes()).isEqualTo(SELLER_IMAGE.getBytes());
    }

    @Test
    public void saveSeller_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/sellers"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    public void saveSeller_WithNonImageFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/sellers")
                        .file(INVALID_SELLER_IMAGE)
                        .param("name", SELLER_REQUEST.name())
                        .param("phoneNumber", SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("image"));

        verifyNoInteractions(service);
    }

    @Test
    public void saveSeller_WithStorageFailure_ReturnsInternalServerError() throws Exception {
        when(service.save(any(SellerRequest.class))).thenThrow(new StorageException("Error uploading file photo.png", null));

        mockMvc.perform(multipart("/sellers")
                        .file(SELLER_IMAGE)
                        .param("name", SELLER_REQUEST.name())
                        .param("phoneNumber", SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void findSellerById_WithExistingId_ReturnsOk() throws Exception {
        when(service.findById(1L)).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(get("/sellers/" + 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SELLER_RESPONSE.name()));
    }

    @Test
    public void findSellerById_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).findById(1L);

        mockMvc.perform(get("/sellers/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void updateSeller_WithExistingId_ReturnsOk() throws Exception {
        when(service.update(1L, UPDATE_SELLER_REQUEST)).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(multipart(PUT, "/sellers/" + 1)
                        .param("name", UPDATE_SELLER_REQUEST.name())
                        .param("phoneNumber", UPDATE_SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SELLER_RESPONSE.name()));
    }

    @Test
    public void updateSeller_WithImage_PassesImageToService() throws Exception {
        var captor = ArgumentCaptor.forClass(UpdateSellerRequest.class);
        when(service.update(eq(1L), any(UpdateSellerRequest.class))).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(multipart(PUT, "/sellers/" + 1)
                        .file(SELLER_IMAGE)
                        .param("name", UPDATE_SELLER_REQUEST.name())
                        .param("phoneNumber", UPDATE_SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isOk());

        verify(service).update(eq(1L), captor.capture());
        assertThat(captor.getValue().image().getBytes()).isEqualTo(SELLER_IMAGE.getBytes());
    }

    @Test
    public void updateSeller_WithNonImageFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart(PUT, "/sellers/" + 1)
                        .file(INVALID_SELLER_IMAGE)
                        .param("name", UPDATE_SELLER_REQUEST.name())
                        .param("phoneNumber", UPDATE_SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("image"));

        verifyNoInteractions(service);
    }

    @Test
    public void updateSeller_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).update(1L, UPDATE_SELLER_REQUEST);

        mockMvc.perform(multipart(PUT, "/sellers/" + 1)
                        .param("name", UPDATE_SELLER_REQUEST.name())
                        .param("phoneNumber", UPDATE_SELLER_REQUEST.phoneNumber()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void listSellers_ReturnsPagedSellers() throws Exception {
        var pageable = PageRequest.of(0, 10);
        when(service.findAll(isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(SELLER_RESPONSE), pageable, 1));

        mockMvc.perform(get("/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value(SELLER_RESPONSE.name()))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10));
    }

    @Test
    public void listSellers_WithPageParams_PassesPageableToService() throws Exception {
        when(service.findAll(isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/sellers").param("page", "2").param("size", "5").param("sort", "name,desc"))
                .andExpect(status().isOk());

        verify(service).findAll(null, PageRequest.of(2, 5, Sort.by("name").descending()));
    }

    @Test
    public void listSellers_WithText_PassesTextToService() throws Exception {
        when(service.findAll(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(SELLER_RESPONSE)));

        mockMvc.perform(get("/sellers").param("text", "joão"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(service).findAll("joão", PageRequest.of(0, 10, Sort.by("id")));
    }

    @Test
    public void deleteSeller_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/sellers/" + 1))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void deleteSeller_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).delete(1L);

        mockMvc.perform(delete("/sellers/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

}