package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByUsername(String username);
    List<User> findAll();
    Optional<User> findById(Long id);
    void save(User user);
    void update(User user);
    void deleteById(Long id);
    void delete(User user);
    boolean existsById(Long id);
    long count();
    List<User> findAllWithRoles();
    Optional<User> findByIdWithRoles(Long id);
    List<User> getAllUsers();
    void saveOrUpdateUser(User user, List<Long> roleIds);
}
