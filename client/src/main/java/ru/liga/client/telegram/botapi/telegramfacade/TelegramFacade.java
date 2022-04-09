package ru.liga.client.telegram.botapi.telegramfacade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.telegram.botapi.botstate.BotState;
import ru.liga.client.telegram.botapi.botstate.BotStateContext;
import ru.liga.client.telegram.botapi.telegramfacade.helper.ButtonHelper;
import ru.liga.client.telegram.cache.UserDataCache;

@Slf4j
@Component
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final ButtonHelper buttonHelper;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache,
                          ButtonHelper buttonHelper) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.buttonHelper = buttonHelper;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        log.info(update.getUpdateId().toString());
        BotApiMethod<?> replyMessage = null;
        try {

            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                log.info("CallbackQuery from User {} CQ {}"
                        , callbackQuery.getFrom().getId(), callbackQuery.getData());

                processCallBackQuery(callbackQuery);
            }

            replyMessage = handleInputMessage(update);
        }catch (Exception e){
            log.info("Ошибка Update {}",update.getUpdateId());
        }

        return replyMessage;
    }

    private BotState chooseBotStateFromInputText(long userId, String inputMessage) {
        BotState botState;
        switch (inputMessage) {
            case "/start":
                botState = BotState.START;
                break;
            case "/profile":
                botState = BotState.SHOW_PROFILE;
                break;
            case "/change":
                botState = BotState.ASK_NAME;
                break;
            case "/search":
                botState = BotState.SEARCH;
                buttonHelper.removeCacheSearch(userId);
                buttonHelper.buttonSearch(userId);
                break;
            case "/lovers":
                botState = BotState.LOVERS;
                buttonHelper.removeCacheLovers(userId);
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
        }
        return botState;
    }

    private void processCallBackQuery(CallbackQuery buttonQuery) {
        final long userId = buttonQuery.getFrom().getId();

        //choose buttons
        switch (buttonQuery.getData()) {
            case "buttonMan":
                buttonHelper.buttonsChooseProfileGender(userId, "Сударъ");
                break;
            case "buttonWoman":
                buttonHelper.buttonsChooseProfileGender(userId, "Сударыня");
                break;
            case "buttonSearchMan":
                buttonHelper.buttonSearchChooseGender(userId, "Сударъ");
                break;
            case "buttonSearchWoman":
                buttonHelper.buttonSearchChooseGender(userId, "Сударыня");
                break;
            case "buttonSearchAll":
                buttonHelper.buttonSearchAll(userId);
                break;
            case "buttonProfile":
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_PROFILE);
                break;
            case "buttonSearch":
                buttonHelper.buttonSearch(userId);
                userDataCache.setUsersCurrentBotState(userId, BotState.SEARCH);
                break;
            case "buttonDislike":
                buttonHelper.buttonDislike(userId);
                break;
            case "buttonLike":
                buttonHelper.buttonLike(userId);
                break;
            case "buttonMenu":
                buttonHelper.buttonMenu(userId);
                break;
            case "buttonChooseLovers":
                userDataCache.setUsersCurrentBotState(userId, BotState.LOVERS);
                break;
            case "buttonLovers":
                buttonHelper.buttonLovers(userId);
                break;
            case "buttonLoved":
                buttonHelper.buttonLoved(userId);
                break;
            case "buttonMatch":
                buttonHelper.buttonMatch(userId);
                break;
            case "buttonBack":
                buttonHelper.buttonBack(userId);
                break;
            case "buttonNext":
                buttonHelper.buttonNext(userId);
                break;
            default:
                userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
        }
    }


    private Long getUserId(Update update) {
        Long id = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            id = message.getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId();
        }
        if (id == null)
            log.info("Не удалось получить userId update {}", update.getUpdateId());
        return id;
    }

    private BotApiMethod<?> handleInputMessage(Update update) {
        Long userId = getUserId(update);

        if (userId == null) return null;

        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        BotApiMethod<?> replyMessage;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String inputMessage = message.getText();
            log.info("Message from User {} Text {}", message.getFrom().getId(), message.getText());
            botState = chooseBotStateFromInputText(userId, inputMessage);
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        try {
            replyMessage = botStateContext.processInputMessage(botState, update, userId);
        } catch (Exception e) {
            userDataCache.removeUser(userId);
            replyMessage = new SendMessage(String.valueOf(userId), "ошибка введите /start");
            log.info("не удалось обработать запрос пользователя {}", userId);
        }

        return replyMessage;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
