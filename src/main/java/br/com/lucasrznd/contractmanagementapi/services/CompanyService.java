package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    /**
     * Fields used by the text search. Each one must have a trigram index (see db/migration).
     */
    private static final String[] SEARCH_FIELDS = {"businessName", "tradeName", "registrationNumber", "cpf", "city"};

    private final ClientCompanyRepository repository;
    private final CompanyMapper mapper;

    public CompanyResponse save(CompanyRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public Page<CompanyResponse> findAll(final String text, final Pageable pageable) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS), pageable).map(mapper::toResponse);
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
