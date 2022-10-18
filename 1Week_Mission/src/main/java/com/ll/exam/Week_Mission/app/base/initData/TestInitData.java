package com.ll.exam.Week_Mission.app.base.initData;

import com.ll.exam.Week_Mission.app.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestInitData implements BaseInitData {
    @Bean
    CommandLineRunner initData(MemberService memberService) {
        return args -> {
            before(memberService);
        };
    }
}