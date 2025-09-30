package ru.kata.spring.boot_security.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.kata.spring.boot_security.services.UserService;

import java.util.stream.Collectors;

@ControllerAdvice
public class CurrentUserAdvice {

    private final UserService userService;

    public CurrentUserAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("email")
    public String email(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return userService.findByUsername(userDetails.getUsername())
                    .map(user -> user.getEmail())
                    .orElse(userDetails.getUsername());
        }
        return "Not authenticated";
    }

    @ModelAttribute("roles")
    public String roles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.joining(", "));
        }
        return "No roles";
    }
}