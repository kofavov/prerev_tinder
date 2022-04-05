package ru.liga.client.telegram.botapi;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.telegram.Bot;

public interface InputMessageHandler {
    BotApiMethod<?> handle(Update update, long userId, Bot bot);
    BotState getHandlerName();
}
