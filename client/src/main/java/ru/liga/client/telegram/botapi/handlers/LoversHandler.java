package ru.liga.client.telegram.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.client.server.ServerClient;
import ru.liga.client.server.ServerClientImpl;
import ru.liga.client.entity.Gender;
import ru.liga.client.entity.User;
import ru.liga.client.service.ImageService;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.botstate.BotState;
import ru.liga.client.telegram.botapi.handlers.helper.ButtonHelper;
import ru.liga.client.telegram.cache.UserDataCache;

import java.io.File;
import java.util.TreeMap;

@Component
public class LoversHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerClient serverClient;
    private final ImageService imageService;
    private final Bot bot;


    public LoversHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                         ServerClient serverClient, ImageService imageService, @Lazy Bot bot) {
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

    @Override
    public BotState getHandlerName() {
        return BotState.LOVERS;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId) {
        User user = prepareUserDataCash(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = null;

        if (botState.equals(BotState.LOVERS)) {
            replyToUser = lovers(userId);
        }

        if (botState.equals(BotState.BACK_LOVERS)
                || botState.equals(BotState.NEXT_LOVERS)) {
            TreeMap<Long, User> loversData = prepareData(userId);
            if (loversData == null || loversData.isEmpty()) {
                return new SendMessage(String.valueOf(userId), "???????????? ????????");
            }
            replyToUser = next(userId, user, loversData);
        }
        return replyToUser;
    }

    private User prepareUserDataCash(long userId) {
        User user = userDataCache.getUserProfileData(userId);
        if (user.getId() == null) {
            user = serverClient.getUserById(userId);
            userDataCache.saveUserProfileData(userId, user);
        }
        return user;
    }

    private SendMessage lovers(long userId) {
        SendMessage replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                "reply.chooseLovers");
        replyToUser.setReplyMarkup(getLoversButtonMarkup());
        return replyToUser;
    }

    private SendMessage next(long userId, User user, TreeMap<Long, User> loversData) {
        User currentLover = loversData.get(userDataCache.getLastLoverId(userId));
        sendImage(userId, currentLover);
        String outputText = currentLover.getGender().getRus() +
                ", " + currentLover.getName() +
                ", " + checkSympathy(user, currentLover);

        SendMessage replyToUser = new SendMessage(String.valueOf(userId), outputText);
        replyToUser.setReplyMarkup(getLoversNavigateButtonsMarkup());
        return replyToUser;
    }

    private String checkSympathy(User user, User currentLover) {
        if (user.getLoved().containsKey(currentLover.getId())
                && user.getLovers().containsKey(currentLover.getId())) {
            return "????????????????????";
        } else if (user.getLoved().containsKey(currentLover.getId())) {
            return "???? ????????????";
        } else if (user.getLovers().containsKey(currentLover.getId())) {
            if(user.getLovers().get(currentLover.getId()).getGender().equals(Gender.FEMALE)){
                return "???????????? ????????";
            }
            return "?????????? ????????";
        } else return "";
    }


    private TreeMap<Long, User> prepareData(long userId) {
        TreeMap<Long, User> loversData = userDataCache.getUserLoversData(userId);

        if (loversData == null || loversData.isEmpty()) return loversData;

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

        buttonLovers.setText("???????????? ????????");
        buttonMatch.setText("????????????????????");
        buttonLoved.setText("???? ????????????");
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

        buttonBack.setText("??????????");
        buttonNext.setText("????????????");
        buttonMenu.setText("????????");
        //Every button must have callBackData, or else not work !
        buttonBack.setCallbackData("buttonBack");
        buttonNext.setCallbackData("buttonNext");
        buttonMenu.setCallbackData("buttonMenu");
        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonBack, buttonMenu, buttonNext);
    }

    private void sendImage(long userId, User currentProfile) {
        File file = imageService.getFile(currentProfile);
        InputFile inputFile = new InputFile(file);
        bot.sendImage(SendPhoto.builder().photo(inputFile)
                .chatId(String.valueOf(userId)).build());
    }
}


