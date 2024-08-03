package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ContractMapper {

    ContractResponse toResponse(final Contract contract);

    @Mapping(target = "id", ignore = true)
    Contract toEntity(final ContractRequest request);

    @Mapping(target = "id", ignore = true)
    Contract update(UpdateContractRequest request, @MappingTarget Contract contract);

}
