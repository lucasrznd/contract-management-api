package br.com.lucasrznd.contractmanagementapi.dtos.response;

import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;

import java.time.LocalDate;

public record ContractResponse(Long id,
                               String companyTradeName,
                               String sellerName,
                               String sellerImageUrl,
                               String advertisingOrder,
                               Double spotDuration,
                               int quantitySpotDay,
                               Double testimonialDuration,
                               int testimonialQuantity,
                               LocalDate startDate,
                               LocalDate endDate,
                               String monthlyPriceFmt,
                               int flashQuantity,
                               String newspaperParticipation,
                               PaymentMethod paymentMethod,
                               Integer paymentDueDay,
                               String observation,
                               String token,
                               String pdfPath) {
}
