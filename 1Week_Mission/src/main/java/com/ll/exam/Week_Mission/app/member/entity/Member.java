package com.ll.exam.Week_Mission.app.member.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "password")
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    private String email;

    public Member (long id) { super(id); }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(nickname!=null){
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        } else {
            authorities.add(new SimpleGrantedAuthority("GENERAL"));
        }

        return authorities;
    }
}
