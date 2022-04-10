package ru.liga.server.service;

import ru.liga.server.entity.User;
import ru.liga.server.repository.UserRepository;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAll();

    User getUserById(long id);

    void saveNewUser(User user);

    void deleteUserById(long id);

    void updateUser(User user);


}
