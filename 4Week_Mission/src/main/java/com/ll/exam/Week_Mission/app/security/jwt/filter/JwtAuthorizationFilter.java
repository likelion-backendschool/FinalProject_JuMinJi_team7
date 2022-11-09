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
        // Token으로부터 회원 정보 가져오기
        /*
          AuthTests에서 헤더를 갖고 올 때 .header("Authorization", "Bearer " + accessToken) 이렇게 가져왔음
          즉 이제 Authorization 헤더에 accessToken만 있는 것이 아니라 Bearer도 있으므로 이것을 제거하고
          accessToken에서 회원정보을 가져와야 됨
        */
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());

            if (jwtProvider.verify(token)) {
                Map<String, Object> claims = jwtProvider.getClaims(token);
                String username = (String) claims.get("username");
                Member member = memberService.findByUsername(username);

                forceAuthentication(member);
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
