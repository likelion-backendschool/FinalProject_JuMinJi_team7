package com.ll.exam.Week_Mission.app.product.mapper;

import com.ll.exam.Week_Mission.app.product.dto.reponse.ProductDto;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "createDate", source = "product.createDate")
    @Mapping(target = "modifyDate", source = "product.updateDate")
    @Mapping(target = "authorId", source = "product.author.id")
    @Mapping(target = "authorName", source = "product.author.nickname")
    @Mapping(target = "subject", source = "product.subject")
    ProductDto entityToProductDto(Product product);


    List<ProductDto> entityToProductDtos(List<Product> products);
}