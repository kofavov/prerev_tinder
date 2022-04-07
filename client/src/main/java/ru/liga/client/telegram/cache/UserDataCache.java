package ru.liga.client.telegram.cache;

import org.springframework.stereotype.Component;
import ru.liga.client.entity.User;
import ru.liga.client.telegram.botapi.botstate.BotState;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, User> usersProfileData = new HashMap<>();
    private final Map<Long, Long> usersLastWatchedProfile = new HashMap<>();
    private final Map<Long, TreeMap<Long, User>> usersProfilesForSearch = new HashMap<>();
    private final Map<Long, Long> usersLastLoverProfile = new HashMap<>();
    private final Map<Long, TreeMap<Long, User>> usersLoversData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }

        return botState;
    }

    @Override
    public User getUserProfileData(long userId) {
        User userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new User();
        }
        return userProfileData;
    }

    @Override
    public void saveUserProfileData(long userId, User userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }

    @Override
    public void removeUser(long userId) {
        usersProfileData.remove(userId);
        usersBotStates.remove(userId);
        usersProfilesForSearch.remove(userId);
        usersLastWatchedProfile.remove(userId);
        usersLoversData.remove(userId);
        usersLastLoverProfile.remove(userId);
    }

    @Override
    public void fillUsersProfilesForSearch(long userId, Map<Long, User> profileForSearch) {
        usersProfilesForSearch.put(userId, new TreeMap<>(profileForSearch));
        usersProfilesForSearch.get(userId).remove(userId);
    }

    @Override
    public void setUsersLastElementForSearch(long userId, Long lastSearchId) {
        usersLastWatchedProfile.put(userId, lastSearchId);
    }

    @Override
    public Long getLastSearchIdForUser(long userId) {
        return usersLastWatchedProfile.get(userId);
    }

    @Override
    public TreeMap<Long, User> getUserProfilesForSearch(long userId) {
        return usersProfilesForSearch.get(userId);
    }

    @Override
    public void removeLastSearchProfile(Long userId) {
        usersLastWatchedProfile.remove(userId);
    }

    @Override
    public void setUsersLastLover(long userId, Long lastLoversId) {
        usersLastLoverProfile.put(userId, lastLoversId);
    }

    @Override
    public void fillUserLoversData(long userId, Map<Long, User> profileForSearch) {
        usersLoversData.put(userId, new TreeMap<>(profileForSearch));
        usersLoversData.get(userId).remove(userId);
    }

    @Override
    public Long getLastLoverId(long userId) {
        return usersLastLoverProfile.get(userId);
    }

    @Override
    public TreeMap<Long, User> getUserLoversData(long userId) {
        return usersLoversData.get(userId);
    }

    @Override
    public void removeLastLoverProfile(long userId) {
        usersLastLoverProfile.remove(userId);
    }


}
