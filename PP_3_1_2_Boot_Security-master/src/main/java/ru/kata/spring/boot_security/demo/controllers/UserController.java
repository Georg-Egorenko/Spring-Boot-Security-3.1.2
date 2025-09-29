package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView userProfile(@AuthenticationPrincipal User user) {
        ModelAndView mav = new ModelAndView();

        if (user == null) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        User fullUser = userService.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));

        mav.addObject("user", fullUser);
        mav.setViewName("user/profile");
        return mav;
    }
}