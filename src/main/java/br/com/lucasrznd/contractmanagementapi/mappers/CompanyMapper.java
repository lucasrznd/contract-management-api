package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = StorageUrlMapper.class)
public interface CompanyMapper {

    @Mapping(target = "urlImage", source = "imageKey", qualifiedByName = "toImageUrl")
    CompanyResponse toResponse(final ClientCompany company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    ClientCompany toEntity(final CompanyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    ClientCompany update(UpdateCompanyRequest request, @MappingTarget ClientCompany clientCompany);

}
