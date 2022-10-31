package com.ll.exam.Week_Mission.app.home.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdminHomeController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/home/main")
    public String showMain() {
        return "adm/home/main";
    }
}
