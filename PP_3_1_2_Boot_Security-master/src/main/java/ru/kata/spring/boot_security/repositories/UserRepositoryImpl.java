package ru.kata.spring.boot_security.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username",
                    User.class
            );
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                User.class
        );
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void deleteById(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public void delete(User user) {
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findAllWithRoles() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                User.class
        );
        return query.getResultList();
    }

    @Override
    public User findByIdWithRoles(Long id) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id",
                    User.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}