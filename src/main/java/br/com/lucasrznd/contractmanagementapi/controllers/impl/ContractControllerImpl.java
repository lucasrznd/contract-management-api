package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.controllers.ContractController;
import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_PDF;

@RestController
@RequiredArgsConstructor
public class ContractControllerImpl implements ContractController {

    private final ContractService service;

    @Override
    public ResponseEntity<ContractResponse> save(ContractRequest request) {
        return ResponseEntity.status(CREATED).body(service.save(request));
    }

    @Override
    public ResponseEntity<List<ContractResponse>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @Override
    public ResponseEntity<List<ContractResponse>> findAllByExpirationNextWeek() {
        return ResponseEntity.ok().body(service.findAllByExpirationNextWeek());
    }

    @Override
    public ResponseEntity<List<ContractResponse>> findLastFive() {
        return ResponseEntity.ok().body(service.findLastFive());
    }

    @Override
    public ResponseEntity<List<ContractResponse>> list(LocalDate startDate, LocalDate endDate, Long companyId, Long sellerId) {
        return ResponseEntity.ok().body(service.list(startDate, endDate, companyId, sellerId));
    }

    @Override
    public ResponseEntity<Integer> countContracts() {
        return ResponseEntity.ok().body(service.countContracts());
    }

    @Override
    public ResponseEntity<String> totalEstimatedRevenue() {
        return ResponseEntity.ok().body(service.totalEstimatedRevenue());
    }

    @Override
    public ResponseEntity<byte[]> getPDF(Long id) {
        byte[] pdfBytes = service.getPDF(id);

        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=" + "CONTRATO.pdf")
                .contentType(APPLICATION_PDF)
                .body(pdfBytes);
    }

    @Override
    public ResponseEntity<ContractResponse> update(Long id, UpdateContractRequest request) {
        return ResponseEntity.ok().body(service.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
