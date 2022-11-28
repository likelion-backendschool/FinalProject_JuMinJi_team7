package com.ll.exam.Week_Mission.app.product.dto.reponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;
    @JsonIgnore
    private long postKeywordId;

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .createDate(product.getCreateDate())
                .modifyDate(product.getUpdateDate())
                .authorId(product.getAuthor().getId())
                .authorName(product.getAuthor().getNickname())
                .subject(product.getSubject())
                .postKeywordId(product.getPostKeyword().getId())
                .build();
    }
}