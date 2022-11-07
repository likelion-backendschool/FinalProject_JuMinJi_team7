package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.jwt.JwtProvider;
import com.ll.exam.Week_Mission.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

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

}
