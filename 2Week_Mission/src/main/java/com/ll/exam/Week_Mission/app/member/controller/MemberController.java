package com.ll.exam.Week_Mission.app.member.controller;

import com.ll.exam.Week_Mission.app.member.dto.request.JoinForm;
import com.ll.exam.Week_Mission.app.member.dto.request.UpdateForm;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.mybook.service.MyBookService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final MyBookService myBookService;

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

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, BindingResult bindingResult) {
        Member oldMember = memberService.findByUsername(joinForm.getUsername());

        if (oldMember!=null) {
            return "redirect:/?errorMsg=" + Ut.url.encode("이미 가입되어 있는 회원입니다.");
        }

        if (!joinForm.getPassword().equals(joinForm.getRePassword())) {
            bindingResult.rejectValue("rePassword", "passwordMismatch",
                    "두 비밀번호가 일치하지 않습니다.");

            return "redirect:/member/join?errorMsg=" + Ut.url.encode(String.valueOf(bindingResult.getFieldError().getDefaultMessage()));
        }

        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail(), joinForm.getNickname());

        return "redirect:/member/login?msg=" + Ut.url.encode("회원가입이 완료되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
        public String showProfile(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberService.findByUsername(memberContext.getUsername());
        model.addAttribute("memberContext", memberContext);
        model.addAttribute("member", member);

        return "member/profile";
        }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberService.findByUsername(memberContext.getUsername());
        model.addAttribute("member", member);
        return "member/profile_update";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @Valid UpdateForm updateForm, BindingResult bindingResult) {
        Member member = memberService.findByUsername(memberContext.getUsername());

        if (!updateForm.getPassword().equals(updateForm.getRePassword())) {
            bindingResult.rejectValue("rePassword", "passwordMismatch",
                    "두 비밀번호가 일치하지 않습니다.");

            return "redirect:/member/modify?errorMsg=" + Ut.url.encode(String.valueOf(bindingResult.getFieldError().getDefaultMessage()));
        }

       memberService.modify(member, updateForm.getPassword(), updateForm.getEmail(), updateForm.getNickname());

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    public String findUsername(String email, Model model) {
        Member member = memberService.findByEmail(email).orElse(null);

        if (member == null) {
            String errorMsg="해당 이메일로 일치하는 회원정보가 없습니다.";
            return "redirect:/member/findUsername?errorMsg=" + Ut.url.encode(errorMsg);
        }

        model.addAttribute("matchingUsername",member.getUsername());
        model.addAttribute("matchingEmail",member.getEmail());

        return "member/findUsername";
    }

    @GetMapping("/mybook")
    public String showMyBook(@AuthenticationPrincipal MemberContext memberContext, Model model){
        Member member = memberContext.getMember();

        List<MyBook> myBooks = myBookService.findByMemberId(member.getId());

        model.addAttribute("myBooks", myBooks);
        return "member/mybook";
    }
}
