package ru.liga.client.telegram.botapi.handlers;

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
public class LoversHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;
    private final ImageService imageService;


    public LoversHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                         ServerController serverController, ImageService imageService) {
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
        return BotState.LOVERS;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId, Bot bot) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId() == null) {
            user = serverController.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = null;

        if (botState.equals(BotState.LOVERS)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                    "reply.chooseLovers");
            replyToUser.setReplyMarkup(getLoversButtonMarkup());
        }

        if (botState.equals(BotState.BACK_LOVERS)
                || botState.equals(BotState.NEXT_LOVERS)) {
            TreeMap<Long, User> loversData = prepareData(userId);
            User currentLover = loversData.get(userDataCache.getLastLoverId(userId));
            sendImage(userId, bot, currentLover);
            String outputText = currentLover.getGender() +
                    ", " + currentLover.getName() +
                    ", " + checkSympathy(user,currentLover);
            replyToUser = new SendMessage(String.valueOf(userId), outputText);
            replyToUser.setReplyMarkup(getLoversNavigateButtonsMarkup());
        }
        return replyToUser;
    }

    private String checkSympathy(User user,User currentLover) {
        if (user.getLoved().containsKey(currentLover.getId())) {
            return "Вы любимы";
        } else if (user.getLovers().containsKey(currentLover.getId())) {
            return "Любим вами";
        } else return "Взаимность";
    }


    private TreeMap<Long, User> prepareData(long userId) {
        TreeMap<Long, User> loversData = userDataCache.getUserLoversData(userId);
        Long lastLoverId = userDataCache.getLastLoverId(userId);
        if (lastLoverId == null) {
            lastLoverId = loversData.firstEntry().getKey();
            userDataCache.setUsersLastLover(userId, lastLoverId);
        }
        return loversData;
    }

    private ReplyKeyboard getLoversButtonMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonLovers = new InlineKeyboardButton();
        InlineKeyboardButton buttonMatch = new InlineKeyboardButton();
        InlineKeyboardButton buttonLoved = new InlineKeyboardButton();

        buttonLovers.setText("Ваши любимцы");
        buttonMatch.setText("Взаимность");
        buttonLoved.setText("Вы любимы");
        //Every button must have callBackData, or else not work !
        buttonLovers.setCallbackData("buttonLovers");
        buttonMatch.setCallbackData("buttonMatch");
        buttonLoved.setCallbackData("buttonLoved");


        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonLovers, buttonMatch, buttonLoved);
    }

    private ReplyKeyboard getLoversNavigateButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonBack = new InlineKeyboardButton();
        InlineKeyboardButton buttonNext = new InlineKeyboardButton();
        InlineKeyboardButton buttonMenu = new InlineKeyboardButton();

        buttonBack.setText("Назад");
        buttonNext.setText("Вперед");
        buttonMenu.setText("Меню");
        //Every button must have callBackData, or else not work !
        buttonBack.setCallbackData("buttonBack");
        buttonNext.setCallbackData("buttonNext");
        buttonMenu.setCallbackData("buttonMenu");
        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonBack, buttonMenu, buttonNext);
    }

    private void sendImage(long userId, Bot bot, User currentProfile) {
        File file = imageService.getFile(currentProfile);
        InputFile inputFile = new InputFile(file);
        bot.sendImage(SendPhoto.builder().photo(inputFile)
                .chatId(String.valueOf(userId)).build());
    }
}


