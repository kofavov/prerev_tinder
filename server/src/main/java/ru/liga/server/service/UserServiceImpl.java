package ru.liga.server.service;

import org.springframework.stereotype.Service;
import ru.liga.server.entity.User;
import ru.liga.server.repository.UserRepository;
import ru.liga.server.translator.Translator;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Translator translator;


    public UserServiceImpl(UserRepository userRepository, Translator translator) {
        this.userRepository = userRepository;
        this.translator = translator;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public void saveNewUser(User user) {
        user.setName(translator.translate(user.getName()));
        user.setHeading(translator.translate(user.getHeading()));
        user.setDescription(translator.translate(user.getDescription()));

        userRepository.save(user);
    }

    @Override
    public void updateUser(User user) {
        user.setName(translator.translate(user.getName()));
        user.setHeading(translator.translate(user.getHeading()));
        user.setDescription(translator.translate(user.getDescription()));

        userRepository.save(user);
    }

    @Override
    public void deleteUserById(long id) {
        try {
            userRepository.deleteUserInLovers(id);
        } catch (Exception ignored) {

        }
        try {
            userRepository.delete(getUserById(id));
        }catch (Exception ignored){

        }
    }

    @Override
    public void deleteLoverById(long user_id, long lover_id) {
        try {
            userRepository.deleteLoversById(user_id,lover_id);
        } catch (Exception ignored) {

        }
    }


    @Override
    public Set<User> getLovers(long id) {
        return userRepository.findAllLoversById(id);
//        return getUserById(id).getThisLovers();
    }

    @Override
    public Set<User> getLovedThisById(long id) {
        return userRepository.findAllLovedById(id);
//        return getUserById(id).getLovedThis();
    }

    @Override
    public void addNewLover(long user_id, long lover_id) {
        try {
            userRepository.saveNewLover(user_id,lover_id);
        }catch (Exception ignore){

        }
    }
}
