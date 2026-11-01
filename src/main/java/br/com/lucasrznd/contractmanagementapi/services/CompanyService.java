package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import br.com.lucasrznd.contractmanagementapi.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static br.com.lucasrznd.contractmanagementapi.storage.StorageFolder.COMPANY_IMAGES;

@Service
@RequiredArgsConstructor
public class CompanyService {

    /**
     * Fields used by the text search. Each one must have a trigram index (see db/migration).
     */
    private static final String[] SEARCH_FIELDS = {"businessName", "tradeName", "registrationNumber", "cpf", "city"};

    private final ClientCompanyRepository repository;
    private final CompanyMapper mapper;
    private final StorageService storageService;

    public CompanyResponse save(CompanyRequest request) {
        ClientCompany company = mapper.toEntity(request);
        String imageKey = storageService.uploadIfPresent(request.image(), COMPANY_IMAGES);
        company.setImageKey(imageKey);

        return mapper.toResponse(storageService.discardOnFailure(imageKey, () -> repository.save(company)));
    }

    public Page<CompanyResponse> findAll(final String text, final Pageable pageable) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS), pageable).map(mapper::toResponse);
    }

    public Integer countCompanies() {
        return Math.toIntExact(repository.count());
    }

    public CompanyResponse findById(final Long id) {
        return mapper.toResponse(find(id));
    }

    public CompanyResponse update(final Long id, final UpdateCompanyRequest request) {
        ClientCompany entity = find(id);
        String oldImageKey = entity.getImageKey();

        ClientCompany updatedCompany = mapper.update(request, entity);
        String newImageKey = storageService.uploadIfPresent(request.image(), COMPANY_IMAGES);
        if (newImageKey == null) {
            return mapper.toResponse(repository.save(updatedCompany));
        }

        updatedCompany.setImageKey(newImageKey);
        CompanyResponse response = mapper.toResponse(storageService.discardOnFailure(newImageKey, () -> repository.save(updatedCompany)));
        storageService.deleteQuietly(oldImageKey);

        return response;
    }

    private ClientCompany find(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + ClientCompany.class.getSimpleName()));
    }

    public void delete(Long id) {
        ClientCompany company = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + CompanyResponse.class.getSimpleName()));

        repository.delete(company);
        storageService.deleteQuietly(company.getImageKey());
    }

}
