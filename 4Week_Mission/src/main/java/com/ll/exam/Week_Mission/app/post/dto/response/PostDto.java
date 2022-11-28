package com.ll.exam.Week_Mission.app.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String subject;
    private String content;
    private String contentHtml;
}
