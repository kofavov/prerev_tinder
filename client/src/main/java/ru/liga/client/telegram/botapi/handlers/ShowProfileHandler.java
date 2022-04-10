package ru.liga.client.telegram.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.server.ServerClient;
import ru.liga.client.server.ServerClientImpl;
import ru.liga.client.entity.User;
import ru.liga.client.service.ImageService;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.botstate.BotState;
import ru.liga.client.telegram.cache.UserDataCache;

import java.io.File;

@Component
public class ShowProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerClient serverClient;
    private final ImageService imageService;
    private final Bot bot;

    public ShowProfileHandler(UserDataCache userDataCache
            , ReplyMessagesService messagesService
            , ServerClient serverClient
            , ImageService imageService,@Lazy Bot bot) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverClient = serverClient;
        this.imageService = imageService;
        this.bot = bot;
    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId) {
        return processUsersInput(update, userId);
    }
    private BotApiMethod<?> processUsersInput(Update update, long userId) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId() == null){
            user = serverClient.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        File file = imageService.getFile(user);
        InputFile inputFile = new InputFile(file);
        bot.sendImage(SendPhoto.builder().photo(inputFile)
                .chatId(String.valueOf(userId)).build());
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        String outputText = user.getGender().getRus() + ", " + user.getName() +
                "\n" + "Для заполнения анкеты заново введите /change" ;
        return new SendMessage(String.valueOf(userId), outputText);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_PROFILE;
    }
}
