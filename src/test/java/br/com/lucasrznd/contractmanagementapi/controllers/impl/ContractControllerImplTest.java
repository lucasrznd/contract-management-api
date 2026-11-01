package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.config.WebConfig;
import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.services.ContractService;
import br.com.lucasrznd.contractmanagementapi.services.PDFService;
import br.com.lucasrznd.contractmanagementapi.services.ZapSignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.ContractConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
class ContractControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService service;

    @MockitoBean
    private PDFService pdfService;

    @MockitoBean
    private ZapSignService zapSignService;

    @MockitoBean
    private WebConfig webConfig;

    @Test
    public void saveContract_WithValidData_ReturnsCreated() throws Exception {
        when(service.save(CONTRACT_REQUEST)).thenReturn(CONTRACT_RESPONSE);

        mockMvc.perform(post("/contracts").contentType(APPLICATION_JSON).content(toJson(CONTRACT_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyTradeName").value(CONTRACT_RESPONSE.companyTradeName()));
    }

    @Test
    public void saveContract_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/contracts").contentType(APPLICATION_JSON).content(toJson(INVALID_CONTRACT_REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()));
    }

    @Test
    public void updateContract_WithExistingId_ReturnsOk() throws Exception {
        when(service.update(1L, UPDATE_CONTRACT_REQUEST)).thenReturn(CONTRACT_RESPONSE);

        mockMvc.perform(put("/contracts/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_CONTRACT_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyTradeName").value(UPDATE_CONTRACT_REQUEST.clientCompany().tradeName()));
    }

    @Test
    public void updateContract_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).update(1L, UPDATE_CONTRACT_REQUEST);

        mockMvc.perform(put("/contracts/" + 1).contentType(APPLICATION_JSON).content(toJson(UPDATE_CONTRACT_REQUEST)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
    }

    @Test
    public void listContracts_ReturnsPagedContracts() throws Exception {
        var pageable = PageRequest.of(0, 10);
        when(service.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(CONTRACT_RESPONSE), pageable, 1));

        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10));
    }

    @Test
    public void listContracts_WithPageParams_PassesPageableToService() throws Exception {
        when(service.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/contracts").param("page", "2").param("size", "5").param("sort", "id,desc"))
                .andExpect(status().isOk());

        verify(service).findAll(PageRequest.of(2, 5, Sort.by("id").descending()));
    }

    @Test
    public void listContracts_WithExpirationNextWeek_ReturnsExpiringContracts() throws Exception {
        when(service.findAllByExpirationNextWeek()).thenReturn(List.of(CONTRACT_RESPONSE));

        mockMvc.perform(get("/contracts/expiration-next-week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void listFiveContracts_ReturnsOk() throws Exception {
        when(service.findLastFive()).thenReturn(Arrays.asList(CONTRACT_RESPONSE, CONTRACT_RESPONSE,
                CONTRACT_RESPONSE, CONTRACT_RESPONSE, CONTRACT_RESPONSE));

        mockMvc.perform(get("/contracts/last-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void listContracts_WithCompanyAndSellerAndDateRange_ReturnsOk() throws Exception {
        when(service.list(LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1), 1L, 1L)).thenReturn(List.of(CONTRACT_RESPONSE));

        mockMvc.perform(get("/contracts/list")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2025-01-01")
                        .param("companyId", "1")
                        .param("sellerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void countContracts_ReturnsOk() throws Exception {
        when(service.countContracts()).thenReturn(1);

        mockMvc.perform(get("/contracts/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    public void getTotalEstimatedRevenue_ReturnsOk() throws Exception {
        when(service.totalEstimatedRevenue()).thenReturn("R$ 1.200,00");

        mockMvc.perform(get("/contracts/total-estimated-revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("R$ 1.200,00"));
    }

    @Test
    public void deleteContract_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/contracts/" + 1))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void deleteContract_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service).delete(1L);

        mockMvc.perform(delete("/contracts/" + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()));
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