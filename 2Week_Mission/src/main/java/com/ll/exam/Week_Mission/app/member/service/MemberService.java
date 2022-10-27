package com.ll.exam.Week_Mission.app.member.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.service.CashService;
import com.ll.exam.Week_Mission.app.email.service.EmailService;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.repository.MemberRepository;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CashService cashService;

    public Member join(String username, String password, String email, String nickname) {

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .build();

        memberRepository.save(member);

        //sendWelcomeEmail(email); // 테스트 진행하는 동안 임의로 주석처리

        return member;
    }

    @SneakyThrows // to catch emailService Errors(UnsupportedEncodingException, RuntimeException)
    private void sendWelcomeEmail(String email) {
        emailService.sendPlainTextEmail(email, emailService.getWelcomeSubject(), emailService.getWelcomeMessage());
    }

    @Transactional(readOnly = true)
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public void modify(Member member, String password, String email, String nickname) {
        member.setPassword(passwordEncoder.encode(password));
        member.setEmail(email);
        member.setNickname(nickname);

        // MemberContext 세션 업데이트
        forceAuthentication(member);
    }

    private void forceAuthentication(Member member) {
        MemberContext memberContext = new MemberContext(member, member.genAuthorities());

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        member.getPassword(),
                        memberContext.getAuthorities()
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public long addCash(Member member, long price, String eventType) {
        CashLog cashLog = cashService.addCash(member, price, eventType);

        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);

        return newRestCash;
    }
}

