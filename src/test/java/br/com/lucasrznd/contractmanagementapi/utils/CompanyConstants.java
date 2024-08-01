package br.com.lucasrznd.contractmanagementapi.utils;

import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;

public abstract class CompanyConstants {

    public static ClientCompany COMPANY_ENTITY = new ClientCompany(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Parana",
            "01153000"
    );

    public static ClientCompany UPDATED_ENTITY = new ClientCompany(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Parana",
            "01153000"
    );

    public static CompanyRequest COMPANY_REQUEST = new CompanyRequest(
            "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Parana",
            "01153000"
    );

    public static UpdateCompanyRequest UPDATE_COMPANY_REQUEST = new UpdateCompanyRequest(
            "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1300, "Parana",
            "01153000"
    );

    public static CompanyResponse COMPANY_RESPONSE = new CompanyResponse(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Parana",
            "01153000"
    );

}