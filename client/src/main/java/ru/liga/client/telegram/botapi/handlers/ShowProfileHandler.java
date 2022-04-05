package ru.liga.client.telegram.botapi.handlers;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.User;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.cache.UserDataCache;
@Component
public class ShowProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;

    public ShowProfileHandler(UserDataCache userDataCache,
                              ReplyMessagesService messagesService
            , ServerController serverController,ApplicationContext applicationContext) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverController = serverController;
    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId()==null){
            user = serverController.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        return new SendMessage(String.valueOf(userId)
                , String.format("%s %s", "Данные по вашей анкете:\n"
                , user));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_PROFILE;
    }
}
