package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                              Model model) {
        User user = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + principal.getUsername()));
        model.addAttribute("user", user);
        return "user/profile";
    }
}