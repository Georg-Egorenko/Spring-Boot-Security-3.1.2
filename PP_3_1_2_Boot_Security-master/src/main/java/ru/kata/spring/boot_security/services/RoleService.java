package ru.kata.spring.boot_security.services;

import ru.kata.spring.boot_security.models.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Role findByName(String name);
    List<Role> findAll();
    void deleteById(Long id);
    void delete(Role role);
    boolean existsById(Long id);
    long count();
    Role findById(Long id);
    Set<Role> findRolesByIds(List<Long> roleIds);
}

