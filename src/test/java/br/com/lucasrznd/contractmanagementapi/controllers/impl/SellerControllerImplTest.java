package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.config.WebConfig;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.services.SellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SellerControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService service;

    @MockBean
    private WebConfig webConfig;

    @Test
    public void saveSeller_WithValidData_ReturnsCreated() throws Exception {
        when(service.save(SELLER_REQUEST)).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(post("/sellers").contentType(APPLICATION_JSON).content(toJson(SELLER_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(SELLER_RESPONSE.name()));
    }

    @Test
    public void saveSeller_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/sellers").contentType(APPLICATION_JSON).content(toJson(INVALID_SELLER_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));
    }

    @Test
    public void updateSeller_WithExistingId_ReturnsOk() throws Exception {
        when(service.update(1L, UPDATE_SELLER_REQUEST)).thenReturn(SELLER_RESPONSE);

        mockMvc.perform(put("/sellers/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_SELLER_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SELLER_RESPONSE.name()));
    }

    @Test
    public void updateSeller_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).update(1L, UPDATE_SELLER_REQUEST);

        mockMvc.perform(put("/sellers/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_SELLER_REQUEST)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void listSellers_ReturnsSellers() throws Exception {
        when(service.findAll()).thenReturn(List.of(SELLER_RESPONSE));

        mockMvc.perform(get("/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
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

    private String toJson(final Object object) throws Exception {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new Exception("Error to convert object to json", e);
        }
    }

}