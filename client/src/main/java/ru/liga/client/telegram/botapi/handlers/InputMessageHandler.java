package ru.liga.client.telegram.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.telegram.botapi.botstate.BotState;

public interface InputMessageHandler {
    BotApiMethod<?> handle(Update update, long userId);
    BotState getHandlerName();
}
