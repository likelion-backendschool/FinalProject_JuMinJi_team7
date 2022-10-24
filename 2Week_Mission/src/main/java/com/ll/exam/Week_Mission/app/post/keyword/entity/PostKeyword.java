package com.ll.exam.Week_Mission.app.post.keyword.entity;


import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

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

    public long getExtra_postTagsCount() {
        return (long) getExtra().get("postTagsCount");
    }
    public long getExtra_countPostsByKeyword() {
        return (long) getExtra().get("countPostsByKeyword");
    }
}
