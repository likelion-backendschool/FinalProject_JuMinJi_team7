package com.ll.exam.Week_Mission.app.member.repository;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
}
