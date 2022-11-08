package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.jwt.JwtProvider;
import com.ll.exam.Week_Mission.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class JwtTests {
    @Value("${spring.custom.jwt.secret-key}")
    private String secretKey;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("secretKey 존재 확인")
    void t1() {
        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("JwtProvider 객체로 hmac 인코딩된 secretKey 객체 생성")
    void t2() {
        SecretKey secretKey = Ut.callMethod(jwtProvider, "getSecretKey");
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

    @Test
    @DisplayName("로그인 후 얻은 JWT 토큰으로 현재 로그인 한 회원정보 가져오기 (JwtProvider.getClaims())")
    void t4() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "12341234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        var mvcResult = resultActions.andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String accessToken = response.getHeader("Authentication");

        System.out.println(jwtProvider.getClaims(accessToken));
    }

    @Test
    @DisplayName("로그인 후 얻은 JWT 토큰으로 현재 로그인 한 회원정보 가져오기 (강제 세션 로그인 -> memberContext)")
    void t5() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "12341234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        var mvcResult = resultActions.andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String accessToken = response.getHeader("Authentication");

        resultActions = mvc
                .perform(
                        get("/api/v1/member/me")
                                .header("Authorization", "Bearer " + accessToken) // value: Bearer + 한 칸 공백 + accessToken
                        /*
                        bearer token은 (Security Header에 담아지는)token 포맷의 일종
                        API에 access하려면 accessToken을 사용해야 하는데, accessToken 액세스 토큰의 유효시간이 다 되면 bearerToken을 사용하여 새 액세스 토큰을 얻는다
                        즉 accessToken을 받으려면 인증 서버에 이 bearerToken을 클라이언트 ID와 함께 보내면 된다
                         */
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("S-1"))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.createDate").isNotEmpty())
                .andExpect(jsonPath("$.data.updateDate").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("user1"))
                .andExpect(jsonPath("$.data.email").value("user1@meotbooks.com"))
                .andExpect(jsonPath("$.data.nickname").value("user1Author"))
                .andExpect(jsonPath("$.fail").value(false))
                .andExpect(jsonPath("$.success").value(true));
    }

}
