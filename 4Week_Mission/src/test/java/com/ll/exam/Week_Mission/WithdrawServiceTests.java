package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.repository.MemberRepository;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import com.ll.exam.Week_Mission.app.withdraw.service.WithdrawService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class WithdrawServiceTests {
    @Autowired
    WithdrawService withdrawService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원의 가장 최근 계좌 가져오기")
    void t1() {
        Member member = memberRepository.findByUsername("user1").get();

        if(member.getRestCash() > 8000) {
            withdrawService.apply(member, "농협", "123412341234", 5000, "유저1");
            withdrawService.apply(member, "기업은행", "432143214321", 3000, "유저");
        } else {
            memberService.addCash(member, 8_000, EventGroup.CHARGE, PayGroup.REMITTANCE, null);
        }

        Withdraw existingWithdraw = withdrawService.findTop1ByMemberIdOrderByIdDesc(member.getId());

        assertThat(existingWithdraw.getBankName()).isEqualTo("기업은행");
        assertThat(existingWithdraw.getBankAccountNo()).isEqualTo("432143214321");
        assertThat(existingWithdraw.getAccountHolder()).isEqualTo("유저");
    }
}
