package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.mappers.CompanyMapper;
import br.com.lucasrznd.contractmanagementapi.repositories.ClientCompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.lucasrznd.contractmanagementapi.creator.CreatorUtils.generateMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService service;

    @Mock
    private ClientCompanyRepository  repository;

    @Mock
    private CompanyMapper mapper;

    @Test
    public void saveCompany_WithValidData_ReturnsCompany() {
        final var request = generateMock(CompanyRequest.class);

        when(mapper.toEntity(request)).thenReturn(new ClientCompany());
        when(repository.save(any(ClientCompany.class))).thenReturn(new ClientCompany());

        service.save(request);

        verify(mapper).toEntity(request);
        verify(repository).save(any(ClientCompany.class));
    }

}