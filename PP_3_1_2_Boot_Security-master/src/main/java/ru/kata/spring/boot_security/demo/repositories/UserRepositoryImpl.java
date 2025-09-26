package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;

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
    public void save(User user) {
        entityManager.persist(user);
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
}