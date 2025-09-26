package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;

@Repository
public interface RoleRepository {
    Role findByName(String name);
    List<Role> findAll();
    Role findById(Long id);
    void deleteById(Long id);
    void delete(Role role);
    boolean existsById(Long id);
    long count();
}
