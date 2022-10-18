package com.ll.exam.Week_Mission.app.base.initData;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;

public interface BaseInitData {
    default void before(MemberService memberService) {
        Member member1 = memberService.join("user1", "1234", "user1@test.com","null");
        Member member2 = memberService.join("user2", "1234", "user2Author", "user2@test.com");
    }
}
