package com.ll.exam.Week_Mission.app.post.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class PostForm {
    @NotEmpty(message="제목을 입력해주세요.")
    private String subject;
    @NotEmpty(message="내용을 입력해주세요.")
    private String content;
    @NotEmpty(message="내용을 입력해주세요.")
    private String contentHtml;
    @NotBlank(message = "해시태그를 하나 이상 입력해주세요.")
    private String postTagContents;
}
