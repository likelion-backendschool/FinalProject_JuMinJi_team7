package com.ll.exam.Week_Mission.app.security.handler;

import com.ll.exam.Week_Mission.util.Ut;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String url = "/member/login?errorMsg=" + Ut.url.encode("아이디나 비밀번호가 일치하지 않습니다. 다시 로그인해주세요");

        redirectStrategy.sendRedirect(request, response, url);
    }
}