package ru.liga.client.telegram.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.entity.User;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.cache.UserDataCache;

@Slf4j
@Component
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
    }

    public BotApiMethod<?> handleUpdate(Update update, Bot bot) {
        BotApiMethod<?> replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("CallbackQuery from User {} CQ {}"
                    , callbackQuery.getFrom().getId(), callbackQuery.getData());
//            return processCallBackQuery(callbackQuery);
            replyMessage = processCallBackQuery(callbackQuery);
        }

        replyMessage = handleInputMessage(update,bot);

        return replyMessage;
    }

    private BotApiMethod<?> processCallBackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;

        //From Gender choose buttons
        if (buttonQuery.getData().equals("buttonMan")) {
            User user = userDataCache.getUserProfileData(userId);
            user.setGender("Сударъ");
            userDataCache.saveUserProfileData(userId, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HEAD);
            callBackAnswer = new SendMessage(String.valueOf(chatId), "Заголовок");
        } else if (buttonQuery.getData().equals("buttonWoman")) {
            User user = userDataCache.getUserProfileData(userId);
            user.setGender("Сударыня");
            userDataCache.saveUserProfileData(userId, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HEAD);
            callBackAnswer = new SendMessage(String.valueOf(chatId), "Заголовок");
        } else if (buttonQuery.getData().equals("buttonProfile")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_PROFILE);

        } else if (buttonQuery.getData().equals("buttonSearch")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.SEARCH);
        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        }

        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


    private BotApiMethod<?> handleInputMessage(Update update, Bot bot) {
        long userId = getUserId(update);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        BotApiMethod<?> replyMessage;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String inputMessage = message.getText();
            log.info("Message from User {} Text {}", message.getFrom().getId(), message.getText());
            switch (inputMessage) {
                case "/start":
                    botState = BotState.START;
                    break;
                case "/name":
                    botState = BotState.ASK_NAME;
                    break;
                case "/gender":
                    botState = BotState.ASK_GENDER;
                    break;
                case "/head":
                    botState = BotState.ASK_HEAD;
                    break;
                case "/desc":
                    botState = BotState.ASK_DESC;
                    break;
                case "/profile":
                    botState = BotState.SHOW_PROFILE;
                    break;
                case "/search":
                    botState = BotState.SEARCH;
                default:
                    botState = userDataCache.getUsersCurrentBotState(userId);
                    break;
            }
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        try {
            replyMessage = botStateContext.processInputMessage(botState, update,userId,bot);
        }catch (Exception e){
            userDataCache.removeUser(userId);
            replyMessage = new SendMessage(String.valueOf(userId),"ошибка введите /start");
            log.info("не удалось обработать запрос пользователя {}",userId);
        }

        return replyMessage;
    }

    private Long getUserId(Update update) {
        Long id = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            id = message.getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId();
        }

        return id;
    }
}
