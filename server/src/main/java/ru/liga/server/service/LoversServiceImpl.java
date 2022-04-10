package ru.liga.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.liga.server.entity.User;
import ru.liga.server.repository.UserRepository;
import ru.liga.server.translator.Translator;

import java.util.Set;

@Slf4j
@Service
public class LoversServiceImpl implements LoversService {
    private final UserRepository userRepository;

    public LoversServiceImpl(UserRepository userRepository, Translator translator) {
        this.userRepository = userRepository;
    }
    @Override
    public void deleteLoverById(long user_id, long lover_id) {
        try {
            userRepository.deleteLoversById(user_id,lover_id);
        } catch (Exception ignored) {
            log.debug("Service deleteLoverById ({},{})",user_id,lover_id);
        }
    }

    @Override
    public Set<User> getLovers(long id) {
        return userRepository.findAllLoversById(id);
    }

    @Override
    public Set<User> getLovedThisById(long id) {
        return userRepository.findAllLovedById(id);
    }

    @Override
    public void addNewLover(long user_id, long lover_id) {
        try {
            userRepository.saveNewLover(user_id,lover_id);
        }catch (Exception ignore){
            log.debug("addNewLover ({},{})",user_id,lover_id);
        }
    }
}
