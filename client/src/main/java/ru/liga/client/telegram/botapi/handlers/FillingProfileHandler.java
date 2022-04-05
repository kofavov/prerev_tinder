package ru.liga.client.telegram.botapi.handlers;

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
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.cache.UserDataCache;

import java.util.ArrayList;
import java.util.List;

@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final ServerController serverController;

    public FillingProfileHandler(UserDataCache userDataCache,
                                 ReplyMessagesService messagesService, ServerController serverController) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.serverController = serverController;
    }

    @Override
    public BotApiMethod<?> handle(Update update, long userId, Bot bot) {
        return processUsersInput(update,userId);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private BotApiMethod<?> processUsersInput(Update update, long userId) {
        String usersAnswer = "";
        Message message = update.getMessage();
        if (message != null && message.hasText()){
            usersAnswer = message.getText().replaceAll("\n"," ");
        }

        User profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.START)){
            User user = serverController.getUserById(userId);
            if (user==null){
                botState = BotState.FILLING_PROFILE;
            }
            else {
                replyToUser = getMessageWhenUserAlreadyExist(userId, user);
                replyToUser.setReplyMarkup(getMenuButtonsMarkup());
                return replyToUser;
            }
        }

        if (botState.equals(BotState.FILLING_PROFILE)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                    "reply.start","reply.askName");
            profileData.setId(userId);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        }

        if (botState.equals(BotState.ASK_NAME)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId),
                    "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        }

        if (botState.equals(BotState.ASK_GENDER)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askGender");
            profileData.setName(usersAnswer);
            replyToUser.setReplyMarkup(getGenderButtonsMarkup());
        }

        if (botState.equals(BotState.ASK_HEAD)){
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askHead");
//            profileData.setGender(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESC);
        }

        if (botState.equals(BotState.ASK_DESC)){
            replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.askDesc");
            profileData.setHeading(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.FILLED_PROFILE);
        }
        if (botState.equals(BotState.FILLED_PROFILE)) {
            profileData.setDescription(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
            serverController.saveNewUser(profileData);
            profileData = serverController.getUserById(userId);

            replyToUser = messagesService.getReplyMessage(String.valueOf(userId), "reply.help");
            replyToUser.setReplyMarkup(getMenuButtonsMarkup());
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
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

        return getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonGenderMan, buttonGenderWoman);
    }

    private InlineKeyboardMarkup getMenuButtonsMarkup(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton();
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton();
        buttonGenderMan.setText("Профиль");
        buttonGenderWoman.setText("Поиск");
        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("buttonProfile");
        buttonGenderWoman.setCallbackData("buttonSearch");

        return getInlineKeyboardMarkup(inlineKeyboardMarkup, buttonGenderMan, buttonGenderWoman);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(InlineKeyboardMarkup inlineKeyboardMarkup, InlineKeyboardButton buttonGenderMan, InlineKeyboardButton buttonGenderWoman) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonGenderMan);
        keyboardButtonsRow1.add(buttonGenderWoman);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
