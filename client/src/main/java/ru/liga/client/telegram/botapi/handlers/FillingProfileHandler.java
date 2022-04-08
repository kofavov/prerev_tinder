package ru.liga.client.telegram.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.User;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.botstate.BotState;
import ru.liga.client.telegram.botapi.handlers.helper.ButtonHelper;
import ru.liga.client.telegram.cache.UserDataCache;

@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;
    private final Bot bot;

    public FillingProfileHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                                 ServerController serverController,@Lazy Bot bot) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverController = serverController;
        this.bot = bot;
    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId) {
        return processUsersInput(update, userId);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId) {
        String usersAnswer = getUsersAnswer(update);

        User profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.START)) {
            User user = serverController.getUserById(userId);
            if (user == null) {
                botState = BotState.FILLING_PROFILE;
            } else {
                replyToUser = getMessageWhenUserAlreadyExist(userId, user);
                replyToUser.setReplyMarkup(getMenuButtonsMarkup());
                return replyToUser;
            }
        }

        if (botState.equals(BotState.FILLING_PROFILE)) {
            replyToUser = startFillingProfile(userId, profileData);
        }

        if (botState.equals(BotState.ASK_NAME)) {
            replyToUser = askName(userId);
        }

        if (botState.equals(BotState.ASK_GENDER)) {
            replyToUser = askGender(userId, usersAnswer, profileData);
        }

        if (botState.equals(BotState.ASK_HEAD)) {
            replyToUser = askHead(userId);
        }

        if (botState.equals(BotState.ASK_DESC)) {
            replyToUser = askDesk(userId, usersAnswer, profileData);
        }
        if(botState.equals(BotState.ASK_FIND_GENDER)){
            replyToUser = askFindGender(userId,usersAnswer,profileData);
        }
        if (botState.equals(BotState.FILLED_PROFILE)) {
            botState = filledProfile(userId, usersAnswer, profileData);
        }
        if (botState.equals(BotState.PRE_SEARCH)) {
            profileData = serverController.getUserById(userId);

            replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.help");
            replyToUser.setReplyMarkup(getMenuButtonsMarkup());
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }

    private String getUsersAnswer(Update update) {
        String usersAnswer = "";
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            usersAnswer = message.getText().replaceAll("\n", " ");
        }
        return usersAnswer;
    }

    private SendMessage startFillingProfile(long userId, User profileData) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                "reply.start", "reply.askName");
        profileData.setId(userId);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        return replyToUser;
    }

    private SendMessage askName(long userId) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                "reply.askName");
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        return replyToUser;
    }

    private SendMessage askGender(long userId, String usersAnswer, User profileData) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askGender");
        profileData.setName(usersAnswer);
        replyToUser.setReplyMarkup(getGenderButtonsMarkup());
        return replyToUser;
    }

    private SendMessage askHead(long userId) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askHead");
//            profileData.setGender(usersAnswer);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESC);
        return replyToUser;
    }

    private SendMessage askDesk(long userId, String usersAnswer, User profileData) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askDesc");
        profileData.setHeading(usersAnswer);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_FIND_GENDER);
        return replyToUser;
    }

    private SendMessage askFindGender(long userId, String usersAnswer, User profileData) {
        SendMessage replyToUser;
        replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.chooseLoversGender");
        profileData.setDescription(usersAnswer);
        replyToUser.setReplyMarkup(getFindGenderButtonsMarkup());
        return replyToUser;
    }

    private BotState filledProfile(long userId, String usersAnswer, User profileData) {
        BotState botState;
//        profileData.setDescription(usersAnswer);
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        serverController.saveNewUser(profileData);
        botState = BotState.PRE_SEARCH;
        return botState;
    }

    private SendMessage getMessageWhenUserAlreadyExist(long userId, User user) {
        SendMessage replyToUser;
        replyToUser = new SendMessage(String.valueOf(userId), String.format("%s %s \n%s",
                "Анкета уже была создана\nДанные по вашей анкете"
                , user
                , messagesService
                        .getReplyMessage(String.valueOf(userId)
                                , "reply.help").getText()));
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        userDataCache.saveUserProfileData(userId, user);
        return replyToUser;
    }

    private InlineKeyboardMarkup getGenderButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton();
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton();
        buttonGenderMan.setText("Сударъ");
        buttonGenderWoman.setText("Сударыня");
        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("buttonMan");
        buttonGenderWoman.setCallbackData("buttonWoman");

        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonGenderMan, buttonGenderWoman);
    }

    private InlineKeyboardMarkup getMenuButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton profile = new InlineKeyboardButton();
        InlineKeyboardButton search = new InlineKeyboardButton();
        InlineKeyboardButton lovers = new InlineKeyboardButton();
        profile.setText("Профиль");
        search.setText("Поиск");
        lovers.setText("Любимцы");

        //Every button must have callBackData, or else not work !
        profile.setCallbackData("buttonProfile");
        search.setCallbackData("buttonSearch");
        lovers.setCallbackData("buttonChooseLovers");

        return ButtonHelper.getInlineKeyboardMarkup(inlineKeyboardMarkup, profile, search, lovers);
    }
    private InlineKeyboardMarkup getFindGenderButtonsMarkup() {
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
