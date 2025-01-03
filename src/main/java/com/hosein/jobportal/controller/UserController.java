package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.exception.DuplicateEmailException;
import com.hosein.jobportal.services.UserService;
import com.hosein.jobportal.services.UserTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        System.out.println(user.getPassword());
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("validationErrors", result.getAllErrors());
            return "redirect:/register";
        }
        try {
            userService.save(user);
        } catch (DuplicateEmailException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }
}
