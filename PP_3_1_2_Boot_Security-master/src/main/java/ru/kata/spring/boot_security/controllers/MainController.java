package ru.kata.spring.boot_security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/admin/users")
    public ModelAndView adminUsers() {
        return new ModelAndView("admin/users");
    }
    @GetMapping("/user")
    public ModelAndView userProfile() {
        return new ModelAndView("user/profile");
    }
}
