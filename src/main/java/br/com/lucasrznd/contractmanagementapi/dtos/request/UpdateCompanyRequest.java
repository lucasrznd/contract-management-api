package br.com.lucasrznd.contractmanagementapi.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record UpdateCompanyRequest(
        @Schema(description = "Business name", example = "Magazine Luiza Sa")
        @NotBlank(message = "Business name cannot be empty")
        @Size(min = 5, max = 30, message = "Business name must contain between 5 and 30 characters")
        String businessName,

        @Schema(description = "Trade name", example = "Magazine Luiza")
        @NotBlank(message = "Trade name cannot be empty")
        @Size(min = 10, max = 50, message = "Trade name must contain between 10 and 50 characters")
        String tradeName,

        @Schema(description = "Registration number", example = "00000000000100")
        @NotBlank(message = "Registration number cannot be empty")
        @Size(min = 14, max = 17, message = "Registration number must contain between 14 and 17 characters")
        String registrationNumber,

        @Schema(description = "State registration", example = "120000385")
        @Size(min = 8, max = 15, message = "State registration must contain between 8 and 15 characters")
        String stateRegistration,

        @Schema(description = "Phone number", example = "43988888888")
        @NotBlank(message = "Phone number cannot be empty")
        @Size(min = 10, max = 14, message = "Phone number must contain between 10 and 14 characters")
        String phoneNumber,

        @Schema(description = "Company email", example = "magalu@mail.com")
        @Email(message = "Invalid email")
        @NotBlank(message = "Email cannot be empty")
        @Size(min = 6, max = 50, message = "Email must contain between 6 and 50 characters")
        String email,

        @Schema(description = "Street name", example = "Rua Principal")
        @NotBlank(message = "Street name cannot be empty")
        @Size(min = 5, max = 50, message = "Street name must contain between 5 and 50 characters")
        String streetName,

        @Schema(description = "Avenue name", example = "Centro")
        @NotBlank(message = "Avenue name cannot be empty")
        @Size(min = 5, max = 30, message = "Avenue name must contain between 5 and 30 characters")
        String avenueName,

        @Schema(description = "Address number", example = "1212")
        @NotNull(message = "Address number cannot be empty")
        Integer number,

        @Schema(description = "State", example = "Parana")
        @NotBlank(message = "State cannot be empty")
        @Size(min = 4, max = 20, message = "State must contain between 4 and 20 characters")
        String state,

        @Schema(description = "Zip code", example = "01153000")
        @NotBlank(message = "Zip code cannot be empty")
        @Size(min = 8, max = 9, message = "Zip code must contain between 8 and 9 characters")
        String zipCode) {
}
