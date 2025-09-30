package ru.kata.spring.boot_security.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.models.Role;
import ru.kata.spring.boot_security.models.User;
import ru.kata.spring.boot_security.repositories.RoleRepository;
import ru.kata.spring.boot_security.repositories.UserRepository;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (!userRepository.findAll().isEmpty()) {
            System.out.println("=== Data already exists, skipping initialization ===");
            return;
        }


        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");
        //возможно нужно что-то вставить (save)


        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRoles(new HashSet<>(Arrays.asList(adminRole, userRole)));
        userRepository.save(admin);


        User regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("user"));
        regularUser.setEmail("user@example.com");
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(regularUser);

        System.out.println("=== Test Users Created ===");
        System.out.println("Admin: admin / admin");
        System.out.println("User: user / user");
        System.out.println("==========================");
    }
}