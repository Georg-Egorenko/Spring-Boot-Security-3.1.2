package ru.kata.spring.boot_security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.models.Role;
import ru.kata.spring.boot_security.models.User;
import ru.kata.spring.boot_security.repositories.RoleRepository;
import ru.kata.spring.boot_security.repositories.UserRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
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
        // Если роли пришли только с ID, находим полные объекты ролей
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> fullRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                if (role.getId() != null) {
                    // Используем getReferenceById для легковесной proxy
                    fullRoles.add(roleRepository.getReferenceById(role.getId()));
                }
            }
            user.setRoles(fullRoles);
        }

        // Шифруем пароль
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

        // ОБНОВЛЕНИЕ РОЛЕЙ - ДОБАВЬТЕ ЭТО
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> fullRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                if (role.getId() != null) {
                    fullRoles.add(roleRepository.getReferenceById(role.getId()));
                }
            }
            existingUser.setRoles(fullRoles);
        }

        // Обновляем остальные поля
        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());

        if (user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(existingUser.getPassword());
        } else {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Исправьте на existingUser
        }

        userRepository.update(existingUser); // Сохраняем existingUser, а не user
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
        return userRepository.findAll();
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

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return findByUsername(principal.getName())
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdWithRoles(Long id) {
        return findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id, Principal principal) {
        // Проверка на удаление самого себя
        if (principal != null) {
            User currentUser = getCurrentUser(principal);
            if (currentUser != null && currentUser.getId().equals(id)) {
                throw new RuntimeException("Cannot delete your own account");
            }
        }
        deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserDtoById(Long id) {
        User user = getUserByIdWithRoles(id);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("age", user.getAge());
        response.put("roles", user.getRoles().stream()
                .map(role -> Map.of("id", role.getId(), "name", role.getName()))
                .collect(Collectors.toList()));

        return response;
    }

}