package com.ll.exam.Week_Mission.app.base.initData;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;

public interface BaseInitData {
    default void before(MemberService memberService) {
        Member member1 = memberService.join("user1", "12341234", "user1@meotbooks.com","null");
        Member member2 = memberService.join("user2", "12341234", "user2@meotbooks.com", "user2Author");
    }
}
