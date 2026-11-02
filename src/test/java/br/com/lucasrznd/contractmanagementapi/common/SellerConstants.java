package br.com.lucasrznd.contractmanagementapi.common;

import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import org.springframework.mock.web.MockMultipartFile;

public class SellerConstants {

    public static final String IMAGE_KEY = "images/sellers/7c9e6679-7425-40de-944b-e07fc1f90ae7.png";

    public static final String IMAGE_URL = "http://localhost:8333/contract-management/" + IMAGE_KEY + "?X-Amz-Signature=abc";

    public static MockMultipartFile SELLER_IMAGE = new MockMultipartFile("image", "photo.png",
            "image/png", new byte[]{1, 2, 3});

    public static MockMultipartFile INVALID_SELLER_IMAGE = new MockMultipartFile("image", "contract.pdf",
            "application/pdf", new byte[]{1, 2, 3});

    public static Seller SELLER_ENTITY = new Seller(1L, "Lucas Rezende",
            "43988887777", IMAGE_KEY);

    public static Seller UPDATED_ENTITY = new Seller(1L, "Lucas F Rezende",
            "43988887777", IMAGE_KEY);

    public static SellerRequest SELLER_REQUEST = new SellerRequest("Lucas F Rezende",
            "43988887777", null);

    public static SellerRequest INVALID_SELLER_REQUEST = new SellerRequest(null,
            null, null);

    public static UpdateSellerRequest UPDATE_SELLER_REQUEST = new UpdateSellerRequest("Lucas F Rezende",
            "43988887777", null);

    public static UpdateSellerRequest UPDATE_SELLER_REQUEST_WITH_IMAGE = new UpdateSellerRequest("Lucas F Rezende",
            "43988887777", SELLER_IMAGE);

    public static SellerResponse SELLER_RESPONSE = new SellerResponse(1L, "Lucas F Rezende",
            "43988887777", IMAGE_URL);

}
