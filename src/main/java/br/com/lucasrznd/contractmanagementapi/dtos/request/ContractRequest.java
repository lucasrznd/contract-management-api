package br.com.lucasrznd.contractmanagementapi.dtos.request;

import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ContractRequest(
        @Schema(description = "Client company")
        @NotNull(message = "Company cannot be null")
        ClientCompany clientCompany,

        @Schema(description = "Advertising order", example = "200")
        @NotNull(message = "Advertising order cannot be null")
        @Size(min = 2, max = 10, message = "Advertising order must contain between 2 and 10 numbers")
        Integer advertisingOrder,

        @Schema(description = "Spot duration", example = "0.30")
        @NotNull(message = "Spot duration cannot be null")
        @Size(min = 2, max = 5, message = "Spot duration must contain between 2 and 5 numbers")
        Double spotDuration,

        @Schema(description = "Quantity spot per day", example = "1 || 2 || 3")
        @NotNull(message = "Quantity spot day cannot be null")
        @Size(min = 1, max = 5, message = "Quantity spot day must contain between 1 and 5 numbers")
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
        @Size(min = 1, max = 5, message = "Flash quantity must contain between 1 and 5 numbers")
        int flashQuantity,

        @Schema(description = "Newspaper participation", example = "15 minutes")
        @NotBlank(message = "Newspaper participation cannot be empty")
        @Size(min = 10, max = 15, message = "Newspaper participation must contain between 10 and 50 characters")
        String newspaperParticipation,

        @Schema(description = "Payment method", example = "[\"BOLETO\", \"PIX\"]")
        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod,

        @Schema(description = "Payment due day", example = "10")
        @NotNull(message = "Payment due day cannot be null")
        @Size(min = 1, max = 2, message = "Payment due day must contain between 1 and 2 characters")
        Integer paymentDueDay,

        @Schema(description = "Observation", example = "In this contract, the company receives 2 spots per month as a gift.")
        String observation) {
}
