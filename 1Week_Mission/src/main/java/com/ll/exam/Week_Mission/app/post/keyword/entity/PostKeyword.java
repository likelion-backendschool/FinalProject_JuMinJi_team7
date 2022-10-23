package com.ll.exam.Week_Mission.app.post.keyword.entity;


import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
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

    @OneToMany(mappedBy = "postKeyword", cascade = CascadeType.REMOVE)
    private List<PostKeyword> postKeywordList = new ArrayList<>();
}
