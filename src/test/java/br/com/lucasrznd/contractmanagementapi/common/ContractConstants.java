package br.com.lucasrznd.contractmanagementapi.common;

import br.com.lucasrznd.contractmanagementapi.dtos.request.ContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateContractRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.ContractResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;

import static br.com.lucasrznd.contractmanagementapi.common.CompanyConstants.COMPANY_ENTITY;
import static br.com.lucasrznd.contractmanagementapi.common.CompanyConstants.COMPANY_RESPONSE;
import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.SELLER_ENTITY;
import static br.com.lucasrznd.contractmanagementapi.common.SellerConstants.SELLER_RESPONSE;

public class ContractConstants {

    public static Contract CONTRACT_ENTITY = new Contract(
            1L, COMPANY_ENTITY, SELLER_ENTITY, "200", 0.30, 1, 0.45, 2, LocalDate.of(2024, 1, 1),
            LocalDate.now().plusDays(7), 500.00, 2, "10 Minutes", PaymentMethod.BOLETO,
            10, "In this contract, the company receives 2 spots per month as a gift.", "", ""
    );

    public static Contract UPDATED_ENTITY = new Contract(
            1L, COMPANY_ENTITY, SELLER_ENTITY, "200", 0.30, 1, 0.45, 2, LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 1), 600.00, 3, "10 Minutes", PaymentMethod.BOLETO,
            10, "In this contract, the company receives 2 spots per month as a gift.", "",  ""
    );

    public static ContractRequest CONTRACT_REQUEST = new ContractRequest(
            COMPANY_RESPONSE, SELLER_RESPONSE, "200", 0.30, 1, 0.45, 2, LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 1), 500.00, 2, "10 Minutes", PaymentMethod.BOLETO,
            10, "In this contract, the company receives 2 spots per month as a gift."
    );

    public static ContractRequest INVALID_CONTRACT_REQUEST = new ContractRequest(
            null, null, null, null, 0, null, 0, null,
            null, null, 0, null, null,
            null, null
    );

    public static UpdateContractRequest UPDATE_CONTRACT_REQUEST = new UpdateContractRequest(
            COMPANY_RESPONSE, SELLER_RESPONSE, "200", 0.30, 1, 0.45, 2, LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 1), 600.00, 3, "10 Minutes", PaymentMethod.BOLETO,
            10, "In this contract, the company receives 2 spots per month as a gift."
    );

    public static ContractResponse CONTRACT_RESPONSE = new ContractResponse(
            1L, COMPANY_ENTITY.getTradeName(), "Lucas Rezende", "", "200", 0.30, 1, 0.45, 2, LocalDate.of(2024, 1, 1),
            LocalDate.now().plusDays(7), "R$ 500,00", 2, "10 Minutes", PaymentMethod.BOLETO,
            10, "In this contract, the company receives 2 spots per month as a gift.", "", ""
    );

    public static List<Contract> CONTRACT_ENTITY_LIST = List.of(CONTRACT_ENTITY);

}
