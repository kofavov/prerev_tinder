package ru.liga.client.telegram.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.telegram.cache.UserDataCache;

@Component
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
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
//            case "/name":
//                botState = BotState.ASK_NAME;
//                break;
//            case "/gender":
//                botState = BotState.ASK_GENDER;
//                break;
//            case "/head":
//                botState = BotState.ASK_HEAD;
//                break;
//            case "/desc":
//                botState = BotState.ASK_DESC;
//                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(id);
                break;
        }
        userDataCache.setUsersCurrentBotState(id,botState);
        replyMessage = botStateContext.processInputMessage(botState,message);

        return replyMessage;
    }
}
