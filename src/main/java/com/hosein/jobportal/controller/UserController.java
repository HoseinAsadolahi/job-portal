package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.services.UserService;
import com.hosein.jobportal.services.UserTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserTypeService userTypeService;
    private final UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("getAllTypes", userTypeService.findAll());
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/new")
    public String register(@Valid @ModelAttribute("user") User user) {
        userService.save(user);
        return "dashboard";
    }
}
