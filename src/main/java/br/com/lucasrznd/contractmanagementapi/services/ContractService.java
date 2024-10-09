package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import br.com.lucasrznd.contractmanagementapi.mappers.ContractMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import response.DocResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository repository;
    private final ContractMapper mapper;
    private final GeneratePDFService pdfService;
    private final ZapSignService zapSignService;
    private static final Logger logger = LogManager.getLogger();

    public ContractResponse save(ContractRequest request) {
        Contract contract = mapper.toEntity(request);
        contract.setPdfPath(generatePDF(contract));

        return mapper.toResponse(repository.save(contract));
    }

    public List<ContractResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<ContractResponse> findAllByExpirationNextWeek() {
        var list = repository.findAll();

        List<Contract> filteredList = list.
                stream()
                .filter(contract -> (contract.getEndDate().isAfter(LocalDate.now()) || contract.getEndDate().isEqual(LocalDate.now()))
                        && (contract.getEndDate().compareTo(LocalDate.now().plusDays(7)) <= 0)).toList();

        return filteredList.stream().map(mapper::toResponse).toList();
    }

    public List<ContractResponse> findLastFive() {
        return repository.findLastFiveOrderByIdDesc().stream().map(mapper::toResponse).toList();
    }

    public List<ContractResponse> list(LocalDate startDate, LocalDate endDate, Long companyId, Long sellerId) {
        return repository
                .findByCompanyAndSellerAndDateRange(startDate, endDate, companyId, sellerId)
                .stream().map(mapper::toResponse).toList();
    }

    public Integer countContracts() {
        return Math.toIntExact(repository.count());
    }

    public String totalEstimatedRevenue() {
        List<Contract> contracts = repository.findAll();

        int monthDiff;
        Double totalRevenue = 0D;

        for (Contract contract : contracts) {
            monthDiff = contract.getEndDate().getMonth().compareTo(contract.getStartDate().getMonth());
            totalRevenue += contract.getMonthlyPrice() * Math.abs(monthDiff);
        }

        return NumberFormat.getCurrencyInstance().format(totalRevenue);
    }

    public byte[] getPDF(final Long id) {
        try {
            Contract contract = find(id);

            File file = new File("");
            if (contract.getPdfPath() != null) {
                file = new File(contract.getPdfPath());
            }

            // If the PDF not exists its generated
            if (!file.exists()) {
                String pdfPath = pdfService.generatePDF(contract);
                contract.setPdfPath(pdfPath);
                repository.save(contract);
                file = new File(pdfPath);
            }

            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String generatePDF(final Contract contract) {
        try {
            String pdfPath = pdfService.generatePDF(contract);
            contract.setPdfPath(pdfPath);

            return pdfPath;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public ContractResponse createDigitalDoc(final Long id) {
        Contract contract = find(id);

        if (contract.getToken() == null) {
            DocResponse docResponse = zapSignService.generateDocument(contract);
            contract.setToken(docResponse.getToken());
        }

        return mapper.toResponse(repository.save(contract));
    }

    public DocResponse getDocByToken(String token) {
        DocResponse docResponse = zapSignService.getDocByToken(token);

        return docResponse;
    }

    public ContractResponse update(final Long id, final UpdateContractRequest request) {
        Contract entity = find(id);
        Contract updatedContract = mapper.update(request, entity);
        updatedContract.setPdfPath(generatePDF(updatedContract));

        return mapper.toResponse(repository.save(updatedContract));
    }

    private Contract find(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + ContractResponse.class.getSimpleName()));
    }

    public void delete(final Long id) {
        repository.delete(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + ContractResponse.class.getSimpleName())));
    }

}
