package com.ll.exam.Week_Mission.app.post.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Post extends BaseEntity {
    private String subject;
    private String content;
    private String contentHtml;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostHashTag> postHashTagList = new ArrayList<>();
}