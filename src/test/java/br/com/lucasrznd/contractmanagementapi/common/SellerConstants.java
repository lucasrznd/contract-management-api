package br.com.lucasrznd.contractmanagementapi.common;

import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;

public class SellerConstants {

    public static Seller SELLER_ENTITY = new Seller(1L, "Lucas Rezende",
            "43988887777", "");

    public static Seller UPDATED_ENTITY = new Seller(1L, "Lucas F Rezende",
            "43988887777", "");

    public static SellerRequest SELLER_REQUEST = new SellerRequest("Lucas F Rezende",
            "43988887777", "");

    public static SellerRequest INVALID_SELLER_REQUEST = new SellerRequest(null,
            null, null);

    public static UpdateSellerRequest UPDATE_SELLER_REQUEST = new UpdateSellerRequest("Lucas F Rezende",
            "43988887777", "");
    
    public static SellerResponse SELLER_RESPONSE = new SellerResponse(1L, "Lucas F Rezende",
            "43988887777", "");

}
