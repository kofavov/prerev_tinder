package ru.liga.client.server;

import ru.liga.client.entity.User;

import java.util.Map;
import java.util.function.Predicate;

public interface ServerClient {
    void saveNewUser(User user);

    User getUserById(long id);

    void fillLovedMap(long id, User user);

    void fillLoversMap(long id, User user);

    Map<Long, User> getAllWithFilter(Predicate<? super User> filter);

    void removeLover(long userId, Long lastProfile);

    void addNewLover(long userId, Long lastProfile);
}
