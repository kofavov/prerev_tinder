package ru.liga.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.liga.server.entity.User;
import ru.liga.server.repository.UserRepository;
import ru.liga.server.translator.Translator;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
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
       saveNewUser(user);
    }

    @Override
    public void deleteUserById(long id) {
        try {
            userRepository.deleteUserInLovers(id);
        } catch (Exception ignored) {
            log.debug("ошибка при удалении user {} из lovers",id);
        }
        try {
            userRepository.delete(getUserById(id));
        }catch (Exception ignored){
            log.debug("ошибка при удалении user {}",id);
        }
    }


}
