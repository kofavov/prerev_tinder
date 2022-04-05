package ru.liga.client.telegram.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.controller.ServerController;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.cache.UserDataCache;

@Component
public class SearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;

    public SearchHandler(UserDataCache userDataCache, ReplyMessagesService messagesService, ServerController serverController) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverController = serverController;
    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId) {
        return processUsersInput(update,userId);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    private SendMessage processUsersInput(Update update, long userId) {
//        String usersAnswer = inputMsg.getText();
//        long userId = inputMsg.getFrom().getId();
//        long chatId = inputMsg.getChatId();
//
//        User profileData = userDataCache.getUserProfileData(userId);
//        BotState botState = userDataCache.getUsersCurrentBotState(userId);
//
//        SendMessage replyToUser = null;

        return null;
    }
}
