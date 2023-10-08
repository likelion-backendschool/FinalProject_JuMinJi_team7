//package com.ll.exam.Week_Mission;
//
//import com.ll.exam.Week_Mission.app.member.controller.MemberController;
//import com.ll.exam.Week_Mission.app.member.entity.Member;
//import com.ll.exam.Week_Mission.app.member.service.MemberService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.containsString;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//public class MemberControllerTests {
//
//    @Autowired
//    private MockMvc mvc;
//    @Autowired
//    private MemberService memberService;
//
//    @Test
//    @DisplayName("새로운 회원 회원가입")
//    void t1() throws Exception {
//        // WHEN
//        ResultActions resultActions = mvc
//                .perform(post("/member/join")
//                        .with(csrf())
//                        .param("username", "user999")
//                        .param("password", "1234")
//                        .param("email", "user999@test.com")
//                        .param("nickname","IamAuthor")
//                )
//                .andDo(print());
//
//        // THEN
//        resultActions
//                .andExpect(status().is3xxRedirection())
//                .andExpect(handler().handlerType(MemberController.class))
//                .andExpect(handler().methodName("join"))
//                .andExpect(redirectedUrlPattern("/member/login?msg=**"));
//
//        assertThat(memberService.findByUsername("user999")!=null).isTrue();
//    }
//
//    @Test
//    @DisplayName("기존에 존재하는 회원은 회원가입 시도 시 메인페이지로 redirect")
//    void t2() throws Exception {
//        // WHEN
//        ResultActions resultActions = mvc
//                .perform(post("/member/join")
//                        .with(csrf())
//                        .param("username", "user2")
//                        .param("password", "1234")
//                        .param("email", "user2@test.com")
//                        .param("nickname","user2Author")
//                )
//                .andDo(print());
//
//        // THEN
//        resultActions
//                .andExpect(status().is3xxRedirection())
//                .andExpect(handler().handlerType(MemberController.class))
//                .andExpect(handler().methodName("join"))
//                .andExpect(redirectedUrlPattern("/?errorMsg=**"));
//    }
//}
//
//
