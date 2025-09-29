package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public ModelAndView listUsers(Principal principal) {
        ModelAndView mav = new ModelAndView("admin/users");
        mav.addObject("users", userService.findAllWithRoles());
        mav.addObject("allRoles", roleService.findAll());

        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(user -> mav.addObject("currentUser", user));
        }

        return mav;
    }

    @GetMapping("/users/new")
    public ModelAndView showCreateForm(Principal principal) {
        ModelAndView mav = new ModelAndView("admin/user-form");
        mav.addObject("user", new User());
        mav.addObject("allRoles", roleService.findAll());

        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(user -> mav.addObject("currentUser", user));
        }

        return mav;
    }

    @GetMapping("/users/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id, Principal principal) {
        ModelAndView mav = new ModelAndView("admin/user-form");

        User user = userService.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        mav.addObject("user", user);
        mav.addObject("allRoles", roleService.findAll());

        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(currentUser -> mav.addObject("currentUser", currentUser));
        }

        return mav;
    }

    @PostMapping("/users/save")
    public ModelAndView saveOrUpdateUser(@ModelAttribute User user,
                                         @RequestParam(value = "roles", required = false) List<Long> roleIds) {

        userService.saveOrUpdateUser(user, roleIds);

        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/users/delete/{id}")
    public ModelAndView showDeleteUserPage(@PathVariable Long id, Principal principal) {
        ModelAndView mav = new ModelAndView("admin/delete-user");

        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));

        mav.addObject("user", user);

        if (principal != null) {
            userService.findByUsername(principal.getName())
                    .ifPresent(currentUser -> mav.addObject("currentUser", currentUser));
        }

        return mav;
    }

    @PostMapping("/users/delete/{id}")
    public ModelAndView deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping
    public ModelAndView adminPanel() {
        return new ModelAndView("redirect:/admin/users");
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