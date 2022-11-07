package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.jwt.JwtProvider;
import com.ll.exam.Week_Mission.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class JwtTests {
    @Value("${spring.custom.jwt.secret-key}")
    private String secretKey;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("secretKey 존재 확인")
    void t1() {
        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("JwtProvider 객체로 hmac 인코딩된 secretKey 객체 생성")
    void t2() {
        SecretKey secretKey = TestUtil.callMethod(jwtProvider, "getSecretKey");
        // SecretKey secretKey = jwtProvider.getSecretKey(); 코드와 동일

        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("accessToken 가져오기")
    void t3() {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1L); // id == 1
        claims.put("username", "test"); // username == test
        claims.put("authorities", Arrays.asList(
                new SimpleGrantedAuthority("ADMIN"),
                new SimpleGrantedAuthority("MEMBER"))
        );

        String accessToken = jwtProvider.generateAccessToken(claims);

        System.out.println("accessToken : " + accessToken);

        assertThat(accessToken).isNotNull();
    }

}
