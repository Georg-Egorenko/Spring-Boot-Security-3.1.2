package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userProfile(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        // Загружаем пользователя с ролями (если нужно)
        User fullUser = userService.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));

        model.addAttribute("user", fullUser);
        return "user/profile";
    }

    @GetMapping("/user")
    public String userProfile(Model model, Principal principal) {
        Optional<User> user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user); // для навбара
        return "user";
    }
}