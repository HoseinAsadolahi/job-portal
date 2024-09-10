package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.services.UserTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserTypeService userTypeService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("getAllTypes", userTypeService.findAll());
        model.addAttribute("user", new User());
        return "register";
    }
}
