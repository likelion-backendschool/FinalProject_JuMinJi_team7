package com.ll.exam.Week_Mission.app.withdraw.controller;

import com.ll.exam.Week_Mission.app.exception.MemberNotFoundException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.app.withdraw.dto.request.WithdrawApplyForm;
import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import com.ll.exam.Week_Mission.app.withdraw.service.WithdrawService;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/withdraw")
public class WithdrawController {
    private final MemberService memberService;
    private final WithdrawService withdrawService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply")
    public String showWithdrawApply(@AuthenticationPrincipal MemberContext memberContext, Model model) {

        Member applicant = memberService.findByUsername(memberContext.getUsername());

        long restCash = applicant.getRestCash();

        Withdraw existingWithdraw = withdrawService.findByMemberId(applicant.getId());

        if(existingWithdraw != null ) {
            model.addAttribute("withdraw", existingWithdraw);
            model.addAttribute("restCash", restCash);
        }

        model.addAttribute("restCash", restCash);
        return "withdraw/apply";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply")
    public String withdrawApply(@Valid WithdrawApplyForm withdrawApplyForm, @AuthenticationPrincipal MemberContext memberContext) {
        Member applicant = memberService.findByUsername(memberContext.getUsername());

        if (applicant == null) {
            throw new MemberNotFoundException("회원 정보를 찾을 수 없습니다.");
        }

        withdrawService.apply(applicant, withdrawApplyForm.getBankName(), withdrawApplyForm.getBankAccountNo(),withdrawApplyForm.getPrice(), withdrawApplyForm.getAccountHolder());

        return "redirect:/member/profile?msg=" + Ut.url.encode("출금 신청이 완료되었습니다.");
    }
}
