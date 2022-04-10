package ru.liga.server.service;

import ru.liga.server.entity.User;

import java.util.Set;

public interface LoversService {
    Set<User> getLovers(long id);

    Set<User> getLovedThisById(long id);

    void addNewLover(long user_id, long lover_id);

    void deleteLoverById(long user_id, long lover_id);
}
