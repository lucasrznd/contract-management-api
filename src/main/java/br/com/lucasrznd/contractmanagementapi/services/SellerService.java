package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.mappers.SellerMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.SellerRepository;
import br.com.lucasrznd.contractmanagementapi.specifications.TextSearchSpecification;
import br.com.lucasrznd.contractmanagementapi.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.lucasrznd.contractmanagementapi.storage.StorageFolder.SELLER_IMAGES;

@Service
@RequiredArgsConstructor
public class SellerService {

    /**
     * Fields used by the text search. Each one must have a trigram index (see db/migration).
     */
    private static final String[] SEARCH_FIELDS = {"name", "phoneNumber"};

    private static final Logger logger = LogManager.getLogger();

    private final SellerRepository repository;
    private final SellerMapper mapper;
    private final StorageService storageService;

    public SellerResponse save(SellerRequest request) {
        Seller seller = mapper.toEntity(request);
        String imageKey = uploadImage(request.image());
        seller.setImageKey(imageKey);

        return mapper.toResponse(saveOrDiscardImage(seller, imageKey));
    }

    public Page<SellerResponse> findAll(final String text, final Pageable pageable) {
        return repository.findAll(TextSearchSpecification.of(text, SEARCH_FIELDS), pageable).map(mapper::toResponse);
    }

    public SellerResponse findById(Long id) {
        return mapper.toResponse(find(id));
    }

    public SellerResponse update(final Long id, final UpdateSellerRequest request) {
        Seller seller = find(id);
        String oldImageKey = seller.getImageKey();

        Seller updatedSeller = mapper.update(request, seller);
        String newImageKey = uploadImage(request.image());
        if (newImageKey == null) {
            return mapper.toResponse(repository.save(updatedSeller));
        }

        updatedSeller.setImageKey(newImageKey);
        SellerResponse response = mapper.toResponse(saveOrDiscardImage(updatedSeller, newImageKey));
        deleteImageQuietly(oldImageKey);

        return response;
    }

    private Seller find(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + SellerResponse.class.getSimpleName()));
    }

    public void delete(Long id) {
        Seller seller = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + SellerResponse.class.getSimpleName()));

        repository.delete(seller);
        deleteImageQuietly(seller.getImageKey());
    }

    private String uploadImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return storageService.upload(image, SELLER_IMAGES);
    }

    /**
     * Saves the seller; if it fails, removes the just uploaded image so no orphan object is left in the storage.
     */
    private Seller saveOrDiscardImage(final Seller seller, final String uploadedImageKey) {
        try {
            return repository.save(seller);
        } catch (RuntimeException e) {
            deleteImageQuietly(uploadedImageKey);
            throw e;
        }
    }

    /**
     * Image cleanup must never fail the seller operation; an orphan object is preferable to a failed request.
     */
    private void deleteImageQuietly(final String imageKey) {
        if (imageKey == null) {
            return;
        }

        try {
            storageService.delete(imageKey);
        } catch (RuntimeException e) {
            logger.warn("Could not delete seller image {}: {}", imageKey, e.getMessage());
        }
    }

}
