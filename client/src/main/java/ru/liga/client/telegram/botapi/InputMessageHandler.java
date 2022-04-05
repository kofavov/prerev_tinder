package ru.liga.client.telegram.botapi;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface InputMessageHandler {
    BotApiMethod<?> handle(Update update, long userId);
    BotState getHandlerName();
}
