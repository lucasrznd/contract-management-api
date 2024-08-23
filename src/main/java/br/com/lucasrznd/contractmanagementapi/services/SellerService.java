package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.controllers.exceptions.ResourceNotFoundException;
import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import br.com.lucasrznd.contractmanagementapi.mappers.SellerMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository repository;
    private final SellerMapper mapper;

    public SellerResponse save(SellerRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public List<SellerResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public SellerResponse update(final Long id, final UpdateSellerRequest request) {
        Seller seller = find(id);

        return mapper.toResponse(repository.save(mapper.update(request, seller)));
    }

    private Seller find(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + SellerResponse.class.getSimpleName()));
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + SellerResponse.class.getSimpleName())));
    }

}
