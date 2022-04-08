package ru.liga.client.telegram.botapi.telegramfacade.helper;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.Gender;
import ru.liga.client.entity.User;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.botstate.BotState;
import ru.liga.client.telegram.cache.UserDataCache;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component
public class ButtonHelper {

    private final UserDataCache userDataCache;
    private final ServerController serverController;
    private final Bot bot;

    public ButtonHelper(UserDataCache userDataCache, ServerController serverController,
                        @Lazy Bot bot) {
        this.userDataCache = userDataCache;
        this.serverController = serverController;
        this.bot = bot;
    }


    public void buttonsChooseProfileGender(long userId, String gender) {
        User user = userDataCache.getUserProfileData(userId);
        if (gender.equals("Сударъ")){
            user.setGender(Gender.MALE);
        }else if (gender.equals("Сударыня")){
            user.setGender(Gender.FEMALE);
        }
        userDataCache.saveUserProfileData(userId, user);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HEAD);
    }

    public void buttonSearchChooseGender(long userId, String gender) {
        removeCacheSearch(userId);
        Gender gen = gender.equals("Сударъ")?Gender.MALE:Gender.FEMALE;
        userDataCache.fillUsersProfilesForSearch(userId,
                serverController.getAllWithFilter(u -> u.getGender().equals(gen)));
        userDataCache.setUsersCurrentBotState(userId, BotState.CHOSEN_LOVERS_GENDER);
    }

    public void buttonSearchAll(long userId) {
        removeCacheSearch(userId);

        userDataCache.fillUsersProfilesForSearch(userId,
                serverController.getAllWithFilter(u -> u.getGender().equals(Gender.MALE)
                        || u.getGender().equals(Gender.FEMALE)));
        userDataCache.setUsersCurrentBotState(userId, BotState.CHOSEN_LOVERS_GENDER);
    }

    public void buttonDislike(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT);
        User user = userDataCache.getUserProfileData(userId);
        Long lastProfile = userDataCache.getLastSearchIdForUser(userId);
        try {
            user.getLovers().remove(lastProfile);
        } catch (Exception e) {
            log.info("Возлюбленный {} юзера {} не удален из кэша", lastProfile, userId);
        }
        serverController.removeLover(userId, lastProfile);

        newLastSearchId(userId, lastProfile);
    }

    public void buttonLike(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT);
        User user = userDataCache.getUserProfileData(userId);
        Long lastProfileId = userDataCache.getLastSearchIdForUser(userId);
        try {
            User lover = userDataCache.getUserProfilesForSearch(userId).get(lastProfileId);
            user.getLovers().put(lastProfileId, lover);
            serverController.addNewLover(userId, lastProfileId);
        } catch (Exception e) {
            log.info("Ошибка при добавлении Возлюбленного {} юзера {} в список возлюбленных",
                    userId, lastProfileId);
        }
        checkSympathy(userId, lastProfileId);
        newLastSearchId(userId, lastProfileId);
    }

    public void buttonNext(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT_LOVERS);
        Long lastLoverId = userDataCache.getLastLoverId(userId);
        newNextLastLoverId(userId, lastLoverId);
    }

    public void buttonBack(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.BACK_LOVERS);
        Long lastLoverId = userDataCache.getLastLoverId(userId);
        newBackLastLoverId(userId, lastLoverId);
    }

    public void buttonMenu(long userId) {
        removeCacheSearch(userId);
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
    }

    public void buttonLovers(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId, user);
        userDataCache.fillUserLoversData(userId, user.getLovers());
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT_LOVERS);
    }

    public void buttonLoved(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId, user);
        userDataCache.fillUserLoversData(userId, user.getLoved());
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT_LOVERS);
    }

    public void buttonMatch(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId, user);
        Map<Long, User> lovers = user.getLovers();
        Map<Long, User> loved = user.getLoved();
        Map<Long, User> match = Maps.filterKeys(lovers, loved::containsKey);
        userDataCache.fillUserLoversData(userId, match);
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT_LOVERS);
    }


    public void removeCacheLovers(long userId) {
        try {
            userDataCache.getUserLoversData(userId).remove(userId);
            userDataCache.removeLastLoverProfile(userId);
        } catch (NullPointerException ignored) {
        }
    }

    public void removeCacheSearch(long userId) {
        try {
            userDataCache.getUserProfilesForSearch(userId).remove(userId);
            userDataCache.removeLastSearchProfile(userId);
        } catch (NullPointerException ignored) {
        }
    }

    private void newLastSearchId(long userId, Long lastProfile) {
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserProfilesForSearch(userId).higherKey(lastProfile);
            userDataCache.setUsersLastElementForSearch(userId, nextProfile);
        }
    }

    private void newNextLastLoverId(long userId, Long lastProfile) {
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserLoversData(userId).higherKey(lastProfile);
            userDataCache.setUsersLastLover(userId, nextProfile);
        }
    }

    private void newBackLastLoverId(long userId, Long lastProfile) {
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserLoversData(userId).lowerKey(lastProfile);
            if (nextProfile == null) {
                nextProfile = userDataCache.getUserLoversData(userId).lastKey();
            }
            userDataCache.setUsersLastLover(userId, nextProfile);
        }
    }

    private void checkSympathy(long userId, Long lastProfileId) {
        TreeMap<Long, User> searchUsers =
                userDataCache.getUserProfilesForSearch(userId);
        User currentSearchProfile = searchUsers.get(lastProfileId);
        serverController.fillLoversMap(lastProfileId, currentSearchProfile);
        currentSearchProfile.setLovers(currentSearchProfile.getLovers());
        if (currentSearchProfile.getLovers().containsKey(userId)) {
            bot.sendMessage(new SendMessage(String.valueOf(userId), "Вы любимы!"));
        }
    }
}
