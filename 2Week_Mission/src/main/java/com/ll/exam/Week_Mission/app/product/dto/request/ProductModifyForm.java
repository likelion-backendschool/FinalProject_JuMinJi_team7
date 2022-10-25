package com.ll.exam.Week_Mission.app.product.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProductModifyForm {
    @NotEmpty(message="제목을 입력해주세요.")
    @Size(max = 50, message = "제목은 50자 이내로만 작성 가능합니다.")
    private String subject;
    @NotNull(message = "가격을 입력해주세요.")
    private int price;
    @NotBlank(message = "상품태그를 하나 이상 입력해주세요.")
    private String productTagContents;
}