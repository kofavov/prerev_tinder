package ru.liga.client.telegram.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.client.entity.User;
import ru.liga.client.service.ReplyMessagesService;
import ru.liga.client.telegram.botapi.BotState;
import ru.liga.client.telegram.botapi.InputMessageHandler;
import ru.liga.client.telegram.cache.UserDataCache;
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;

    public FillingProfileHandler(UserDataCache userDataCache,
                                 ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
//        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
//            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
//        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        long userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        User profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.FILLING_PROFILE)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(chatId),
                    "reply.start","reply.askName");
            profileData.setId(userId);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        }

        if (botState.equals(BotState.ASK_NAME)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(chatId),
                    "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
        }

        if (botState.equals(BotState.ASK_GENDER)) {
            replyToUser = messagesService.getReplyMessage(String.valueOf(chatId), "reply.askGender");
            profileData.setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HEAD);
        }

        if (botState.equals(BotState.ASK_HEAD)){
            replyToUser = messagesService.getReplyMessage(String.valueOf(chatId), "reply.askHead");
            profileData.setGender(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESC);
        }

        if (botState.equals(BotState.ASK_DESC)){
            replyToUser = messagesService.getReplyMessage(String.valueOf(chatId), "reply.askDesc");
            profileData.setHeading(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_PROFILE);
        }
        if (botState.equals(BotState.SHOW_PROFILE)) {
            profileData.setDescription(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.FILLED_PROFILE);
            replyToUser = new SendMessage(String.valueOf(chatId), String.format("%s %s", "Данные по вашей анкете", profileData));
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }
}