package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import br.com.lucasrznd.contractmanagementapi.mappers.ContractMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository repository;
    private final ContractMapper mapper;

    public ContractResponse save(ContractRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public List<ContractResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<ContractResponse> findAllByExpirationNextWeek() {
        var list = repository.findAll();

        List<Contract> filteredList = list.
                stream()
                .filter(contract -> (contract.getEndDate().isAfter(LocalDate.now()) || contract.getEndDate().isEqual(LocalDate.now()))
                        && (contract.getEndDate().compareTo(LocalDate.now().plusDays(7)) <= 0)
                        && contract.getEndDate().getMonth().equals(LocalDate.now().getMonth()))
                .toList();

        return filteredList.stream().map(mapper::toResponse).toList();
    }

    public Integer countContracts() {
        return Math.toIntExact(repository.count());
    }

    public ContractResponse update(final Long id, final UpdateContractRequest request) {
        Contract entity = find(id);

        return mapper.toResponse(repository.save(mapper.update(request, entity)));
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
