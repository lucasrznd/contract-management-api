package br.com.lucasrznd.contractmanagementapi.dtos.request;

import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.time.LocalDate;

@With
public record ContractRequest(
        @Schema(description = "Client company")
        @NotNull(message = "Company cannot be null")
        CompanyResponse clientCompany,

        @Schema(description = "Seller")
        @NotNull(message = "Seller cannot be null")
        SellerResponse seller,

        @Schema(description = "Advertising order", example = "200")
        @NotNull(message = "Advertising order cannot be null")
        Integer advertisingOrder,

        @Schema(description = "Spot duration", example = "0.30")
        @NotNull(message = "Spot duration cannot be null")
        Double spotDuration,

        @Schema(description = "Quantity spot per day", example = "1 || 2 || 3")
        @NotNull(message = "Quantity spot day cannot be null")
        int quantitySpotDay,

        @Schema(description = "Contract start date", example = "2024/01/01")
        @NotNull(message = "Start date cannot be null")
        LocalDate startDate,

        @Schema(description = "Contract end Date", example = "2024/03/01")
        @NotNull(message = "End date cannot be null")
        LocalDate endDate,

        @Schema(description = "Monthly price", example = "500.00")
        @NotNull(message = "Monthly price cannot be null")
        Double monthlyPrice,

        @Schema(description = "Flash quantity", example = "1 || 2 || 3")
        @NotNull(message = "Flash quantity cannot be null")
        int flashQuantity,

        @Schema(description = "Newspaper participation", example = "15 minutes")
        @NotBlank(message = "Newspaper participation cannot be empty")
        @Size(min = 10, max = 15, message = "Newspaper participation must contain between 10 and 50 characters")
        String newspaperParticipation,

        @Schema(description = "Payment method", example = "BOLETO")
        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod,

        @Schema(description = "Payment due day", example = "10")
        @NotNull(message = "Payment due day cannot be null")
        Integer paymentDueDay,

        @Schema(description = "Observation", example = "In this contract, the company receives 2 spots per month as a gift.")
        String observation) {
}
