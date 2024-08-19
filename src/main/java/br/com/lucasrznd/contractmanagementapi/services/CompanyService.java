package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ClientCompanyRepository repository;
    private final CompanyMapper mapper;

    public CompanyResponse save(CompanyRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public List<CompanyResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public Integer countCompanies() {
        return Math.toIntExact(repository.count());
    }

    public CompanyResponse update(final Long id, final UpdateCompanyRequest request) {
        ClientCompany entity = find(id);

        return mapper.toResponse(repository.save(mapper.update(request, entity)));
    }

    private ClientCompany find(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + ClientCompany.class.getSimpleName()));
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + CompanyResponse.class.getSimpleName())));
    }

}
