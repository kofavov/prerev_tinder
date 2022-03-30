package ru.liga.server.service;

import ru.liga.server.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAll();

    User getUserById(long id);

    Set<User> getLovers(long id);

    Set<User> getLovedThisById(long id);

    void addNewLover(long user_id, long lover_id);

    void saveNewUser(User user);

    void deleteUserById(long id);

    void deleteLoverById(long user_id, long lover_id);

    void updateUser(User user);
}
