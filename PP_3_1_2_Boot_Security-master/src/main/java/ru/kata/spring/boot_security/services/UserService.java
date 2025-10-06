package ru.kata.spring.boot_security.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.models.User;

import java.security.Principal;
import java.util.List;
import java.util.Map;
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
    User getCurrentUser(Principal principal);
    User getUserById(Long id);
    User getUserByIdWithRoles(Long id);
    void deleteUserById(Long id, Principal principal);
    Map<String, Object> getUserDtoById(Long id);

}
