package com.ll.exam.Week_Mission.app.security.dto;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
public class MemberContext extends User {
    private final Long id;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final String username;
    private final String email;
    private final String nickname;

    public MemberContext(Member member, Map<Integer, GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities.values());
        this.id = member.getId();
        this.createDate = member.getCreateDate();
        this.updateDate = member.getUpdateDate();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }

    public Member getMember() {
        return Member
                .builder()
                .id(id)
                .createDate(createDate)
                .updateDate(updateDate)
                .username(username)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
