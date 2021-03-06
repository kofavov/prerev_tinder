package ru.liga.client.telegram;


import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.client.telegram.botapi.telegramfacade.TelegramFacade;

import java.util.Arrays;

@Slf4j
public class Bot extends TelegramWebhookBot {
    private final String name;
    private final String token;
    private final String webHookPath;

    private final TelegramFacade telegramFacade;

    public Bot(String name, String token, String webHookPath, TelegramFacade telegramFacade) {
        this.name = name;
        this.token = token;
        this.webHookPath = webHookPath;
        this.telegramFacade = telegramFacade;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    public void sendImage(SendPhoto message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(),e);
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }
}
