package br.com.lucasrznd.contractmanagementapi.dtos.response;

import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;

import java.time.LocalDate;

public record ContractResponse(Long id,
                               String companyBusinessName,
                               String sellerName,
                               String sellerImageUrl,
                               Integer advertisingOrder,
                               Double spotDuration,
                               int quantitySpotDay,
                               LocalDate startDate,
                               LocalDate endDate,
                               String monthlyPriceFmt,
                               int flashQuantity,
                               String newspaperParticipation,
                               PaymentMethod paymentMethod,
                               Integer paymentDueDay,
                               String observation
) {
}
