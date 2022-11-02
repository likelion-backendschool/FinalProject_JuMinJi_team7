package com.ll.exam.Week_Mission.app.withdraw.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WithdrawApplyForm {
    @NotBlank(message = "은행명을 입력해주세요.")
    private String bankName;

    @NotBlank(message = "계좌번호를 입력해주세요.")
    private String bankAccountNo;

    @NotNull(message = "출금 신청 금액을 입력해주세요.")
    private int price;
}
