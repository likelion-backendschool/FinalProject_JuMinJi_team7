package com.ll.exam.Week_Mission.app.post.domain.keyword.entity;


import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PostKeyword extends BaseEntity {
    private String content;

    public Object getListUrl() {
        return "/post/tag/" + content;
    }

    public long getExtra_countPostsByKeyword() {
        return (long) getExtra().get("countPostsByKeyword");
    }
}
