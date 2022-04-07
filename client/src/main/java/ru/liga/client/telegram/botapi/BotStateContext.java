package ru.liga.client.telegram.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.telegram.Bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public BotApiMethod<?> processInputMessage(BotState currentState, Update update, long userId, Bot bot) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(update, userId,bot);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.START);
        }
        if (isSearchState(currentState)){
            return messageHandlers.get(BotState.SEARCH);
        }
        if (isLoversState(currentState)){
            return messageHandlers.get(BotState.LOVERS);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isSearchState(BotState currentState) {
        switch (currentState){
            case SEARCH:
            case CHOOSE_LOVERS_GENDER:
            case CHOSEN_LOVERS_GENDER:
            case NEXT:
                return true;
            default:return false;
        }
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case START:
            case ASK_NAME:
            case ASK_HEAD:
            case ASK_GENDER:
            case ASK_DESC:
            case FILLING_PROFILE:
            case FILLED_PROFILE:
            case PRE_SEARCH:
                return true;
            default:
                return false;
        }
    }

    private boolean isLoversState(BotState botState){
        switch (botState){
            case LOVERS:
            case CHOSEN_IN_LOVERS_MENU:
            case BACK_LOVERS:
            case NEXT_LOVERS:
                return true;
            default:
                return false;
        }
    }
}

