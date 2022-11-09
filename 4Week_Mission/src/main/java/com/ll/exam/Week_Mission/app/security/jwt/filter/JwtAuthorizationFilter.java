package com.ll.exam.Week_Mission.app.security.jwt.filter;

import com.ll.exam.Week_Mission.app.security.jwt.JwtProvider;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*  Token으로부터 회원 정보 가져오기 */
        // 헤더 -> .header("Authorization", "Bearer " + accessToken)
        // accessToken에서 회원 정보 가져오려면 Authentication에서 Bearer 제거 필요
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());

            if (jwtProvider.verify(token)) {
                Map<String, Object> claims = jwtProvider.getClaims(token);
                /* 토큰 유효성 1차 검증 */
                // JWT의 장점인 DB없이 CPU에서 정보 조회하는 방법은 아니지만 정보 불일치 문제 해결가능
                Member member = memberService.findByUsername(claims.get("username").toString());

                /* 토큰 유효성 2차 검증 */
                // 화이트리스트에 포함되는지
                if ( memberService.verifyWithWhiteList(member, token) ) {
                    forceAuthentication(member);
                }
            }
        }

        filterChain.doFilter(request, response);
        return;
    }

    private void forceAuthentication(Member member) { // Todo: MemberService.forceAuthentication()과 동일 -> 중복 제거
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

//    private final List<RequestMatcher> excludedMatchers = List.of(
//            new AntPathRequestMatcher("/api/*/member/login"));
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return excludedMatchers.stream()
//                .anyMatch(matcher -> matcher.matches(request));
//    }
}
