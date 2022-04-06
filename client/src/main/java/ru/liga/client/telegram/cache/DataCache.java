package ru.liga.client.telegram.cache;

import ru.liga.client.entity.User;
import ru.liga.client.telegram.botapi.BotState;

import java.util.LinkedHashMap;
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

    void removeLastProfile(Long userId);
}
