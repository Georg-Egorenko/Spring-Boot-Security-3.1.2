package ru.kata.spring.boot_security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.models.User;
import ru.kata.spring.boot_security.services.RoleService;
import ru.kata.spring.boot_security.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;


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


        mav.addObject("currentUser", userService.getCurrentUser(principal));

        return mav;
    }

    @GetMapping("/users/new")
    public ModelAndView showCreateForm(Principal principal) {
        ModelAndView mav = new ModelAndView("admin/user-form");
        mav.addObject("user", new User());
        mav.addObject("allRoles", roleService.findAll());
        mav.addObject("currentUser", userService.getCurrentUser(principal));

        return mav;
    }

    @GetMapping("/users/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id, Principal principal) {
        ModelAndView mav = new ModelAndView("admin/user-form");

        User user = userService.getUserByIdWithRoles(id);
        mav.addObject("user", user);
        mav.addObject("allRoles", roleService.findAll());
        mav.addObject("currentUser", userService.getCurrentUser(principal));

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

        User user = userService.getUserById(id);
        mav.addObject("user", user);
        mav.addObject("currentUser", userService.getCurrentUser(principal));

        return mav;
    }

    @PostMapping("/users/delete/{id}")
    public ModelAndView deleteUser(@PathVariable Long id, Principal principal) {
        // Вся логика проверок и удаления - в сервисе
        userService.deleteUserById(id, principal);
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping
    public ModelAndView adminPanel() {
        return new ModelAndView("redirect:/admin/users");
    }

    @GetMapping("/users/api/{id}")
    @ResponseBody
    public Map<String, Object> getUserForEdit(@PathVariable Long id) {
        return userService.getUserDtoById(id);
    }
}