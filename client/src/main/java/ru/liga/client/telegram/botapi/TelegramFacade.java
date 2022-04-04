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

    public BotApiMethod<?> handleUpdate(Update update) {
        BotApiMethod<?> replyMessage = null;

        if(update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("CallbackQuery from User {} CQ {}"
                    , callbackQuery.getFrom().getId(), callbackQuery.getData());
            return processCallBackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
             log.info("Message from User {} Text {}",message.getFrom().getId(),message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private BotApiMethod<?> processCallBackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;

        //From Destiny choose buttons
//        if (buttonQuery.getData().equals("buttonYes")) {
//            callBackAnswer = new SendMessage(chatId, "Как тебя зовут ?");
//            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE);
//        } else if (buttonQuery.getData().equals("buttonNo")) {
//            callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
//        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
//            callBackAnswer = sendAnswerCallbackQuery("Данная кнопка не поддерживается", true, buttonQuery);
//        }

        //From Gender choose buttons
        if (buttonQuery.getData().equals("buttonMan")) {
            User user = userDataCache.getUserProfileData(userId);
            user.setGender("Сударъ");
            userDataCache.saveUserProfileData(userId, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESC);
            callBackAnswer = new SendMessage(String.valueOf(chatId), "Заголовок");
        } else if (buttonQuery.getData().equals("buttonWoman")) {
            User user = userDataCache.getUserProfileData(userId);
            user.setGender("Сударыня");
            userDataCache.saveUserProfileData(userId, user);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESC);
            callBackAnswer = new SendMessage(String.valueOf(chatId), "Заголовок");
        }
//        else {
//            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
//        }


        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        long id = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;
        switch (inputMessage){
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
            case "/search" :
                botState = BotState.SEARCH;
            default:
                botState = userDataCache.getUsersCurrentBotState(id);
                break;
        }
        userDataCache.setUsersCurrentBotState(id,botState);
        replyMessage = botStateContext.processInputMessage(botState,message);

        return replyMessage;
    }
}
