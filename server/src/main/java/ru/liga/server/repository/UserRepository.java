package ru.liga.server.repository;

import lombok.SneakyThrows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.liga.server.entity.User;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    String queryGetLovers = "select id,name,heading,gender,description from users " +
            "inner join lovers l on users.id = l.lover_id " +
            "where user_id = ?1";
    String queryGetLoved = "select id,name,heading,gender,description from users " +
            "inner join lovers l on users.id = l.user_id " +
            "where lover_id = ?1";
    String saveNewLover = "INSERT INTO lovers (user_id, lover_id)" +
            "VALUES (?1,?2)";
    String deleteLovers = "DELETE FROM lovers WHERE user_id = ?1 AND lover_id = ?2";
    String deleteUserInLovers = "DELETE FROM lovers WHERE user_id = ?1 OR lover_id = ?1";

    @Query(value = queryGetLovers,nativeQuery = true)
    Set<User> findAllLoversById(long id);

    @Query(value = queryGetLoved,nativeQuery = true)
    Set<User> findAllLovedById(long id);

    @Query(value = saveNewLover,nativeQuery = true)
    void saveNewLover(long user_id, long lover_id);

    @Query(value = deleteLovers,nativeQuery = true)
    void deleteLoversById(long user_id,long lover_id);

    @Query(value = deleteUserInLovers,nativeQuery = true)
    void deleteUserInLovers(long user_id);
}
