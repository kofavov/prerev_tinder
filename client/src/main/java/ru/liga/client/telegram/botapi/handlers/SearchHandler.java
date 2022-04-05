package ru.liga.client.telegram.botapi.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.User;
import ru.liga.client.service.ImageService;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.cache.UserDataCache;

import java.io.File;

@Component
@Slf4j
public class SearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;
    private final ImageService imageService;


    public SearchHandler(UserDataCache userDataCache, ReplyMessagesService messagesService
            , ServerController serverController, ImageService imageService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverController = serverController;
        this.imageService = imageService;

    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId, Bot bot) {
        return processUsersInput(update, userId,bot);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId, Bot bot) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId()==null){
            user = serverController.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        File file = imageService.getFile(user);
        InputFile inputFile = new InputFile(file);
        bot.sendImage(SendPhoto.builder().photo(inputFile)
                .chatId(String.valueOf(userId)).build());
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        String outputText = user.getGender() + ", " + user.getName();
        return new SendMessage(String.valueOf(userId), outputText);
    }
}
