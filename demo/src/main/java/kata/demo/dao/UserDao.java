package kata.demo.dao;


import kata.demo.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getAllUsers();

    void saveUser(User user);


    User getUserById(long id);

    void deleteUser(long id);
}