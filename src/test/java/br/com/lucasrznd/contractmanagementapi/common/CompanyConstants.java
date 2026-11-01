package br.com.lucasrznd.contractmanagementapi.common;

import br.com.lucasrznd.contractmanagementapi.dtos.request.CompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateCompanyRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.CompanyResponse;
import br.com.lucasrznd.contractmanagementapi.entities.ClientCompany;
import org.springframework.mock.web.MockMultipartFile;

public abstract class CompanyConstants {

    public static final String COMPANY_IMAGE_KEY = "images/companies/2f1b6d4e-8c3a-4b7e-9d21-5a6c7e8f9012.png";

    public static final String COMPANY_IMAGE_URL = "http://localhost:8333/contract-management/" + COMPANY_IMAGE_KEY + "?X-Amz-Signature=abc";

    public static MockMultipartFile COMPANY_IMAGE = new MockMultipartFile("image", "logo.png",
            "image/png", new byte[]{1, 2, 3});

    public static MockMultipartFile INVALID_COMPANY_IMAGE = new MockMultipartFile("image", "contract.pdf",
            "application/pdf", new byte[]{1, 2, 3});

    public static ClientCompany COMPANY_ENTITY = new ClientCompany(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "00000000000", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Jacarezinho", "Parana",
            "01153000", COMPANY_IMAGE_KEY
    );

    public static ClientCompany UPDATED_ENTITY = new ClientCompany(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "00000000000", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Jacarezinho",
            "Parana", "01153000", COMPANY_IMAGE_KEY
    );

    public static CompanyRequest COMPANY_REQUEST = new CompanyRequest(
            "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "00000000000", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Jacarezinho",
            "Parana", "01153000", null
    );

    public static CompanyRequest INVALID_COMPANY_REQUEST = new CompanyRequest(
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null
    );

    public static UpdateCompanyRequest UPDATE_COMPANY_REQUEST = new UpdateCompanyRequest(
            "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "00000000000", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1300, "Jacarezinho",
            "Parana", "01153000", null
    );

    public static UpdateCompanyRequest UPDATE_COMPANY_REQUEST_WITH_IMAGE = UPDATE_COMPANY_REQUEST.withImage(COMPANY_IMAGE);

    public static CompanyResponse COMPANY_RESPONSE = new CompanyResponse(
            1L, "Magazine Luiza Sa", "Magazine Luiza", "00000000000100", "00000000000", "120000385",
            "43988888888", "magalu@mail.com", "Rua Principal", "Centro", 1212, "Jacarezinho",
            "Parana", "01153000", COMPANY_IMAGE_URL
    );

}
