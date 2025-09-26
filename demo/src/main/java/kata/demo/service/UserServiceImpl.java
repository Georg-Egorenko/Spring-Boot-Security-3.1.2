package kata.demo.service;

import kata.demo.dao.UserDao;
import kata.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public void createUser(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        userDao.saveUser(user);
    }

    @Override
    public void updateUser(long id, String firstName, String lastName, String email) {
        User user = userDao.getUserById(id);{
            if( user == null){
                user = new User();
            }
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
        }
        userDao.saveUser(user);
    }

    @Override
    public void deleteUser(long id) {
        userDao.deleteUser(id);

    }
}
