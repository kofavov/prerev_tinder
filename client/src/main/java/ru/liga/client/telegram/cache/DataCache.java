package ru.liga.client.telegram.cache;

import ru.liga.client.entity.User;
import ru.liga.client.telegram.botapi.botstate.BotState;

import java.util.Map;
import java.util.TreeMap;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(Long userId);
    User getUserProfileData(long userId);
    void saveUserProfileData(long userId, User userProfileData);
    void removeUser(long userId);

    void fillUsersProfilesForSearch(long userId, Map<Long,User> profileForSearch);
    void setUsersLastElementForSearch(long userId, Long lastSearchId);
    Long getLastSearchIdForUser(long userId);
    TreeMap<Long,User> getUserProfilesForSearch(long userId);
    void removeLastSearchProfile(Long userId);

    void setUsersLastLover(long userId, Long lastLoversId);
    void fillUserLoversData(long userId,Map<Long,User> profileForSearch);
    Long getLastLoverId(long userId);
    TreeMap<Long,User> getUserLoversData(long userId);
    void removeLastLoverProfile(long userId);
}
