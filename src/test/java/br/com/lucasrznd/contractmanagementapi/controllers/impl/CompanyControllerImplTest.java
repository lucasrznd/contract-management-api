package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.CompanyConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

        mockMvc.perform(post("/companies").contentType(APPLICATION_JSON).content(toJson(COMPANY_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessName").value(COMPANY_RESPONSE.businessName()));
    }

    @Test
    public void saveCompany_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/companies").contentType(APPLICATION_JSON).content(toJson(INVALID_COMPANY_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));
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

        mockMvc.perform(put("/companies/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_COMPANY_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value(COMPANY_RESPONSE.businessName()));
    }

    @Test
    public void updateCompany_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).update(1L, UPDATE_COMPANY_REQUEST);

        mockMvc.perform(put("/companies/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_COMPANY_REQUEST)))
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

    private String toJson(final Object object) throws Exception {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new Exception("Error to convert object to json", e);
        }
    }

}