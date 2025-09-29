package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional
public class RoleRepositoryImpl implements RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        try {
            TypedQuery<Role> query = entityManager.createQuery(
                    "SELECT r FROM Role r WHERE r.name = :name",
                    Role.class
            );
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error finding role by name: " + name, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r ORDER BY r.id",
                Role.class
        );
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Transactional
    @Override
    public void save(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
        } else {
            entityManager.merge(role);
        }
    }

    @Transactional
    @Override
    public void update(Role role) {
        entityManager.merge(role);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Role role = findById(id);
        if (role != null) {

            if (role.getUsers() != null) {
                role.getUsers().forEach(user -> user.getRoles().remove(role));
            }
            entityManager.remove(role);
        }
    }

    @Override
    @Transactional
    public void delete(Role role) {
        if (role != null) {
            Role managedRole = entityManager.contains(role) ? role : entityManager.merge(role);


            if (managedRole.getUsers() != null) {
                managedRole.getUsers().forEach(user -> user.getRoles().remove(managedRole));
            }
            entityManager.remove(managedRole);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM Role r",
                Long.class
        );
        return query.getSingleResult();
    }
}