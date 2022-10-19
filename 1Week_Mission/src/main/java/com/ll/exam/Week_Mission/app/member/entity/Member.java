package com.ll.exam.Week_Mission.app.member.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private String nickname;
    @ColumnDefault("3")
    private Integer authLevel;
    public Member (long id) { super(id); }
    @PrePersist
    public void prePersist() {
        this.authLevel = this.authLevel == null ? 3 : this.authLevel;
    }
}
