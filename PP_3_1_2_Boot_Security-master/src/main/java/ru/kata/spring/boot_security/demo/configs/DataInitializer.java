package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        userService.findAll().forEach(user -> userService.deleteById(user.getId()));
        roleService.findAll().forEach(role -> roleService.deleteById(role.getId()));

        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");
        roleService.save(adminRole);
        roleService.save(userRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin")); // Реальный хэш
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRoles(new HashSet<>(Arrays.asList(adminRole, userRole)));
        userService.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user")); // Реальный хэш
        user.setEmail("user@example.com");
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userService.save(user);

        System.out.println("=== Test Users Created ===");
        System.out.println("Admin: admin / admin");
        System.out.println("User: user / user");
        System.out.println("==========================");
    }
}
