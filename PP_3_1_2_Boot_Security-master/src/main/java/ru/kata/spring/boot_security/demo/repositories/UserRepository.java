package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User findByUsername(String username);
    List<User> findAll();
    User findById(Long id);
    void save(User user);
    void update(User user);
    void deleteById(Long id);
    void delete(User user);
    boolean existsById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAllWithRoles();
    User findByIdWithRoles(Long id);

}
