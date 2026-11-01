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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.lucasrznd.contractmanagementapi.storage.StorageFolder.COMPANY_IMAGES;

@Service
@RequiredArgsConstructor
public class CompanyService {

    /**
     * Fields used by the text search. Each one must have a trigram index (see db/migration).
     */
    private static final String[] SEARCH_FIELDS = {"businessName", "tradeName", "registrationNumber", "cpf", "city"};

    private static final Logger logger = LogManager.getLogger();

    private final ClientCompanyRepository repository;
    private final CompanyMapper mapper;
    private final StorageService storageService;

    public CompanyResponse save(CompanyRequest request) {
        ClientCompany company = mapper.toEntity(request);
        String imageKey = uploadImage(request.image());
        company.setImageKey(imageKey);

        return mapper.toResponse(saveOrDiscardImage(company, imageKey));
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
        String newImageKey = uploadImage(request.image());
        if (newImageKey == null) {
            return mapper.toResponse(repository.save(updatedCompany));
        }

        updatedCompany.setImageKey(newImageKey);
        CompanyResponse response = mapper.toResponse(saveOrDiscardImage(updatedCompany, newImageKey));
        deleteImageQuietly(oldImageKey);

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
        deleteImageQuietly(company.getImageKey());
    }

    private String uploadImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return storageService.upload(image, COMPANY_IMAGES);
    }

    /**
     * Saves the company; if it fails, removes the just uploaded image so no orphan object is left in the storage.
     */
    private ClientCompany saveOrDiscardImage(final ClientCompany company, final String uploadedImageKey) {
        try {
            return repository.save(company);
        } catch (RuntimeException e) {
            deleteImageQuietly(uploadedImageKey);
            throw e;
        }
    }

    /**
     * Image cleanup must never fail the company operation; an orphan object is preferable to a failed request.
     */
    private void deleteImageQuietly(final String imageKey) {
        if (imageKey == null) {
            return;
        }

        try {
            storageService.delete(imageKey);
        } catch (RuntimeException e) {
            logger.warn("Could not delete company image {}: {}", imageKey, e.getMessage());
        }
    }

}
