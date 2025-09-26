package kata.demo.service;

import kata.demo.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    void createUser(String firstName, String lastName, String email);
    void updateUser(long id, String firstName, String lastName, String email);
    void deleteUser(long id);

}
