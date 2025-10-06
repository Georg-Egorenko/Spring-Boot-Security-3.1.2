package ru.kata.spring.boot_security.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<User> findAll();
    Optional<User> findById(Long id);
    void save(User user);
    void update(User user);
    void deleteById(Long id);

    List<User> findAllWithRoles();
    Optional<User> findByIdWithRoles(Long id);

    Optional<User> findByUsername(String username);
    boolean existsById(Long id);
}