package com.ll.exam.Week_Mission.app.member.controller;

import com.ll.exam.Week_Mission.app.base.dto.RsData;
import com.ll.exam.Week_Mission.app.member.dto.request.LoginForm;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class ApiMemberController {
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    /* JWT login */
    @PostMapping("/login")
    public ResponseEntity<RsData> login(@Valid @RequestBody LoginForm loginForm) {

        Member member = memberService.findByUsername(loginForm.getUsername());

        if (member == null) {
            return Ut.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        if (passwordEncoder.matches(loginForm.getPassword(), member.getPassword()) == false) {
            return Ut.spring.responseEntityOf(RsData.of("F-3", "비밀번호가 일치하지 않습니다."));
        }

        // token 가져오기
        String accessToken = memberService.genAccessToken(member);

        // Authentication 헤더 추가
        HttpHeaders headers = Ut.spring.httpHeadersOf("Authentication", accessToken);

        // response body와 헤더 채워서 리턴
        return Ut.spring.responseEntityOf(RsData.successOf(
                Ut.mapOf(
                        "accessToken", accessToken
                )
        ), headers);
    }

    @GetMapping("/me")
    public ResponseEntity<RsData> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) {
            return Ut.spring.responseEntityOf(RsData.failOf(null));
        }

        return Ut.spring.responseEntityOf(RsData.successOf(memberContext));
    }
}
