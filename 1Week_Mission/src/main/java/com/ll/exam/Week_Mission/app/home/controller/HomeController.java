package com.ll.exam.Week_Mission.app.home.controller;

import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String showMain(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        model.addAttribute("memberContext", memberContext);
        return "home";
    }
}
