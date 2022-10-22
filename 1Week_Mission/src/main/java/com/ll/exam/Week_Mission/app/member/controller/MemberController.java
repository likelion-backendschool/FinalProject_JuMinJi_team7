package com.ll.exam.Week_Mission.app.member.controller;

import com.ll.exam.Week_Mission.app.email.service.EmailService;
import com.ll.exam.Week_Mission.app.member.dto.request.JoinForm;
import com.ll.exam.Week_Mission.app.member.dto.request.UpdateForm;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {return "member/join";}

    @SneakyThrows // to catch emailService Errors(UnsupportedEncodingException, RuntimeException)
    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        Member oldMember = memberService.findByUsername(joinForm.getUsername());

        if (oldMember!=null) {
            return "redirect:/?errorMsg=" + Ut.url.encode("이미 가입되어 있는 회원입니다.");
        }

        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail(), joinForm.getNickname());

        emailService.sendPlainTextEmail(joinForm.getEmail(), emailService.getWelcomeSubject(), emailService.getWelcomeMessage());

        return "redirect:/member/login?msg=" + Ut.url.encode("회원가입이 완료되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
        public String showProfile(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberService.findByUsername(memberContext.getUsername());
        model.addAttribute("member", member);
            return "member/profile";
        }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberService.findByUsername(memberContext.getUsername());
        model.addAttribute("member", member);
        return "member/profile_update";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @Valid UpdateForm updateForm) {
        Member member = memberService.findByUsername(memberContext.getUsername());

        memberService.modify(member,updateForm.getPassword(), updateForm.getEmail(), updateForm.getNickname());

        return "redirect:/member/profile";
    }
}
