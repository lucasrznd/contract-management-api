package br.com.lucasrznd.contractmanagementapi.dtos.response;

public record CompanyResponse(Long id,
                              String businessName,
                              String tradeName,
                              String registrationNumber,
                              String stateRegistration,
                              String phoneNumber,
                              String email,
                              String streetName,
                              String avenueName,
                              Integer number,
                              String city,
                              String state,
                              String zipCode) {
}
