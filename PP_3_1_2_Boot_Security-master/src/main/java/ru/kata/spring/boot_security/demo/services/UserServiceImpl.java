package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        User user = userRepository.findById(id);
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        User existingUser = userRepository.findById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }

        if (user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.update(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(User user) {
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.findById(id) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.findAll().size();
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAllWithRoles() {

        return userRepository.findAllWithRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithRoles(Long id) {

        User user = userRepository.findByIdWithRoles(id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }


    public void saveOrUpdateUser(User user, List<Long> roleIds) {
        if (roleIds != null) {

            Set<Role> roles = roleIds.stream()
                    .map(roleId -> roleService.findById(roleId).orElse(null))
                    .filter(role -> role != null)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        if (user.getId() == null) {
            save(user);
        } else {
            update(user);
        }
    }

}