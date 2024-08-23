package br.com.lucasrznd.contractmanagementapi.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerRequest(
        @Schema(description = "Name of the seller", example = "Lucas Rezende")
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 5, max = 50, message = "Name must contain between 5 and 50 characters")
        String name,

        @Schema(description = "Phone Number", example = "43977777777")
        @NotBlank(message = "Phone Number cannot be empty")
        @Size(min = 10, max = 11, message = "Phone Number must contain between 10 and 11 characters")
        String phoneNumber,

        @Schema(description = "Url Image of the seller")
        String urlImage) {
}
