package ru.liga.client.telegram.cache;

import ru.liga.client.entity.User;
import ru.liga.client.telegram.botapi.BotState;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    User getUserProfileData(long userId);

    void saveUserProfileData(long userId, User userProfileData);
}
