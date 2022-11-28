package com.ll.exam.Week_Mission.app.mybook.dto.response;

import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.product.dto.reponse.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MyBookDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductDto product;

    public static MyBookDto toDto(MyBook myBook) {
        ProductDto productDto = ProductDto.toDto(myBook.getProduct());

        return MyBookDto.builder()
                .id(myBook.getId())
                .createDate(myBook.getCreateDate())
                .modifyDate(myBook.getUpdateDate())
                .ownerId(myBook.getMember().getId())
                .product(productDto)
                .build();
    }
}
