package com.ll.exam.Week_Mission.app.post.dto.response;

import com.ll.exam.Week_Mission.app.post.entity.Post;
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

    public static PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .content(post.getContent())
                .contentHtml(post.getContentHtml())
                .build();
    }
}
