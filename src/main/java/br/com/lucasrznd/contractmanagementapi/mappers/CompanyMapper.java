package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CompanyMapper {

    CompanyResponse toResponse(final ClientCompany company);

    @Mapping(target = "id", ignore = true)
    ClientCompany toEntity(final CompanyRequest request);

}
