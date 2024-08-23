package br.com.lucasrznd.contractmanagementapi.controllers.impl;

import br.com.lucasrznd.contractmanagementapi.controllers.SellerController;
import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.services.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class SellerControllerImpl implements SellerController {

    private final SellerService service;

    @Override
    public ResponseEntity<SellerResponse> save(SellerRequest request) {
        return ResponseEntity.status(CREATED).body(service.save(request));
    }

    @Override
    public ResponseEntity<List<SellerResponse>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @Override
    public ResponseEntity<SellerResponse> update(Long id, UpdateSellerRequest request) {
        return ResponseEntity.ok().body(service.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
