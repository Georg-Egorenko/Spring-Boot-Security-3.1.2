package ru.kata.spring.boot_security.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.models.Role;

import java.util.List;

@Repository
public interface RoleRepository {
    Role findByName(String name);
    List<Role> findAll();
    Role findById(Long id);
    @Transactional
    void save(Role role);
    @Transactional
    void update(Role role);
    void deleteById(Long id);
    void delete(Role role);
    boolean existsById(Long id);
    long count();
    Role getReferenceById(Long id);
}
