package com.ll.exam.Week_Mission.app.member.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class JoinForm {
    @NotEmpty(message="아이디를 입력해주세요.")
    private String username;
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password;
    @NotEmpty(message = "확인용 비밀번호를 입력해주세요.")
    private String rePassword;
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;
    private String nickname;
}
