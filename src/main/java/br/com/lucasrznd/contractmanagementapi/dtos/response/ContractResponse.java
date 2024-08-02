package br.com.lucasrznd.contractmanagementapi.dtos.response;

import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;

import java.time.LocalDate;

public record ContractResponse(Long id,
                               ClientCompany clientCompany,
                               Integer advertisingOrder,
                               Double spotDuration,
                               int quantitySpotDay,
                               LocalDate startDate,
                               LocalDate endDate,
                               Double monthlyPrice,
                               int flashQuantity,
                               String newspaperParticipation,
                               PaymentMethod paymentMethod,
                               Integer paymentDueDay,
                               String observation
) {
}
