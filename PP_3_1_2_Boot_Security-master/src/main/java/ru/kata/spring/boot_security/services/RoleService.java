package ru.kata.spring.boot_security.services;

import ru.kata.spring.boot_security.models.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Role findByName(String name);
    List<Role> findAll();
    Optional<Role> findById(Long id);
    void deleteById(Long id);
    void delete(Role role);
    boolean existsById(Long id);
    long count();

    Set<Role> findRolesByIds(List<Long> roleIds);
}

