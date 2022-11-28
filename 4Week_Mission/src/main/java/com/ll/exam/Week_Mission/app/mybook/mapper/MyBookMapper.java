package com.ll.exam.Week_Mission.app.mybook.mapper;

import com.ll.exam.Week_Mission.app.mybook.dto.response.MyBookDto;
import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.product.dto.reponse.ProductDto;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyBookMapper {
    MyBookMapper INSTANCE = Mappers.getMapper(MyBookMapper.class);

    @Mapping(target = "id", source = "myBook.id")
    @Mapping(target = "createDate", source = "myBook.createDate")
    @Mapping(target = "modifyDate", source = "myBook.updateDate")
    @Mapping(target = "ownerId", source = "myBook.member.id")
    @Mapping(target = "product", source = "myBook.product")
    MyBookDto entityToMyBookDto(MyBook myBook);


    List<MyBookDto> entityToMyBookDtos(List<MyBook> myBooks);
}