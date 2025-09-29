package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAllWithRoles());
        model.addAttribute("allRoles", roleService.findAll());

        // Добавляем currentUser для навбара
        if (principal != null) {
            Optional<User> currentUser = userService.findByUsername(principal.getName());
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        }

        return "admin/users";
    }


    @GetMapping("/users/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/user-form";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findByIdWithRoles(id).orElseThrow(() ->
                new RuntimeException("User not found with id: " + id));

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveOrUpdateUser(@ModelAttribute User user,
                                   @RequestParam(value = "roles", required = false) List<Long> roleIds) {

        if (roleIds != null) {
            user.setRoles(roleIds.stream()
                    .map(roleId -> roleService.findById(roleId).orElse(null))
                    .filter(role -> role != null)
                    .collect(Collectors.toSet()));
        }

        if (user.getId() == null) {
            userService.save(user);
        } else {
            userService.update(user);
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String showDeleteUserPage(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        model.addAttribute("user", user);
        return "admin/delete-user";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping
    public String adminPanel() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users/api/{id}")
    @ResponseBody
    public Map<String, Object> getUserForEdit(@PathVariable Long id) {
        User user = userService.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("age", user.getAge());
        response.put("roles", user.getRoles().stream()
                .map(role -> Map.of("id", role.getId(), "name", role.getName()))
                .collect(Collectors.toList()));

        return response;
    }
}