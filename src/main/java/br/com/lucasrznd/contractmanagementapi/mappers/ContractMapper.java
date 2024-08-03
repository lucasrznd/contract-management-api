package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import org.mapstruct.*;

import java.text.NumberFormat;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {CompanyMapper.class})
public interface ContractMapper {

    @Mapping(target = "companyBusinessName", source = "clientCompany.businessName")
    @Mapping(target = "monthlyPriceFmt", expression = "java(setMonthlyPriceFmt(contract))")
    ContractResponse toResponse(final Contract contract);

    @Mapping(target = "id", ignore = true)
    Contract toEntity(final ContractRequest request);

    @Mapping(target = "id", ignore = true)
    Contract update(UpdateContractRequest request, @MappingTarget Contract contract);

    default String setMonthlyPriceFmt(Contract contract) {
        return NumberFormat.getCurrencyInstance().format(contract.getMonthlyPrice());
    }

}
