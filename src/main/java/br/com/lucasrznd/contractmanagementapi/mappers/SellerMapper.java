package br.com.lucasrznd.contractmanagementapi.mappers;

import br.com.lucasrznd.contractmanagementapi.dtos.request.SellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.request.UpdateSellerRequest;
import br.com.lucasrznd.contractmanagementapi.dtos.response.SellerResponse;
import br.com.lucasrznd.contractmanagementapi.entities.Seller;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = StorageUrlMapper.class)
public interface SellerMapper {

    @Mapping(target = "urlImage", source = "imageKey", qualifiedByName = "toImageUrl")
    SellerResponse toResponse(final Seller seller);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    Seller toEntity(final SellerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageKey", ignore = true)
    Seller update(final UpdateSellerRequest request, @MappingTarget Seller seller);

}
