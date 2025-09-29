package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roleRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Role role = roleRepository.findById(id);
        if (role != null) {
            if (role.getUsers() != null) {
                role.getUsers().forEach(user -> user.getRoles().remove(role));
            }
            roleRepository.delete(role);
        }
    }

    @Override
    @Transactional
    public void delete(Role role) {
        if (role != null) {

            if (role.getUsers() != null) {
                role.getUsers().forEach(user -> user.getRoles().remove(role));
            }
            roleRepository.delete(role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return roleRepository.findById(id) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return roleRepository.findAll().size();
    }

    @Override
    public Set<Role> findRolesByIds(List<Long> roleIds) {
        throw new UnsupportedOperationException("This method should be in RoleService");
    }

}