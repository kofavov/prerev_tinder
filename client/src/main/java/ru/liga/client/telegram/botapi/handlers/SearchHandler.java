package ru.liga.client.telegram.botapi.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.User;
import ru.liga.client.service.ImageService;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.botapi.handlers.helper.ButtonHelper;
import ru.liga.client.telegram.cache.UserDataCache;

import java.io.File;
import java.util.TreeMap;

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
        return processUsersInput(update, userId, bot);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId, Bot bot) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId() == null) {
            user = serverController.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = null;

        if (botState.equals(BotState.SEARCH)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                    "reply.chooseLoversGender");
            replyToUser.setReplyMarkup(getGenderButtonsMarkup());
        }

        if (botState.equals(BotState.CHOSEN_LOVERS_GENDER) ||
                botState.equals(BotState.NEXT)) {
            TreeMap<Long, User> searchUsers =
                    userDataCache.getUserProfilesForSearch(userId);
            Long lastProfileId = userDataCache.getLastSearchIdForUser(userId);

            if (lastProfileId == null) {
                lastProfileId = searchUsers.firstEntry().getKey();
                userDataCache.setUsersLastElementForSearch(userId,lastProfileId);
            }

            User currentProfile = searchUsers.get(lastProfileId);
            File file = imageService.getFile(currentProfile);
            InputFile inputFile = new InputFile(file);
            bot.sendImage(SendPhoto.builder().photo(inputFile)
                    .chatId(String.valueOf(userId)).build());
            String outputText = currentProfile.getGender() + ", " + currentProfile.getName();
            replyToUser = new SendMessage(String.valueOf(userId), outputText);
            replyToUser.setReplyMarkup(getNavigateButtons());

        }


        return replyToUser;
    }

    private ReplyKeyboard getNavigateButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonDislike = new InlineKeyboardButton();
        InlineKeyboardButton buttonLike = new InlineKeyboardButton();
        InlineKeyboardButton buttonMenu = new InlineKeyboardButton();
        buttonDislike.setText("\uD83D\uDC4E");//dislike
        buttonLike.setText("❤");//like
        buttonMenu.setText("Меню");
        //Every button must have callBackData, or else not work !
        buttonDislike.setCallbackData("buttonDislike");
        buttonLike.setCallbackData("buttonLike");
        buttonMenu.setCallbackData("buttonMenu");
        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonDislike, buttonMenu, buttonLike);
    }


    private InlineKeyboardMarkup getGenderButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton();
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton();
        InlineKeyboardButton buttonAll = new InlineKeyboardButton();
        buttonGenderMan.setText("Сударя");
        buttonGenderWoman.setText("Сударыню");
        buttonAll.setText("Всех");

        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("buttonSearchMan");
        buttonGenderWoman.setCallbackData("buttonSearchWoman");
        buttonAll.setCallbackData("buttonSearchAll");
        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonGenderMan, buttonGenderWoman, buttonAll);
    }
}
