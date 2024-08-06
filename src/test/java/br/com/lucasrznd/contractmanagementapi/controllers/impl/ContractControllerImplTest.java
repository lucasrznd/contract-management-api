package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.services.ContractService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.lucasrznd.contractmanagementapi.common.ContractConstants.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ContractControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService service;

    @Test
    public void saveContract_WithValidData_ReturnsCreated() throws Exception {
        when(service.save(CONTRACT_REQUEST)).thenReturn(CONTRACT_RESPONSE);

        mockMvc.perform(post("/contracts").contentType(APPLICATION_JSON).content(toJson(CONTRACT_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyBusinessName").value(CONTRACT_RESPONSE.companyBusinessName()));
    }

    @Test
    public void saveContract_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/contracts").contentType(APPLICATION_JSON).content(toJson(INVALID_CONTRACT_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));
    }

    private String toJson(final Object object) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new Exception("Error to convert object to json", e);
        }
    }

}