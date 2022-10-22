package com.ll.exam.Week_Mission.app.member.dto.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class JoinForm {
    @NotBlank(message = "아이디는 필수 입력사항입니다.")
    @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "[a-zA-Z0-9]*",message = "아이디는 숫자 또는 문자만 가능합니다.")
    private String username;
    @NotBlank(message = "비밀번호는 필수 입력사항입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
    private String password;
    @NotBlank(message = "확인용 비밀번호는 필수 입력사항입니다.")
    private String rePassword;
    @NotBlank(message = "이메일은 필수 입력사항입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;
    private String nickname;
}
