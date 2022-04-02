package ru.liga.client.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.client.controller.MainController;
import ru.liga.client.entity.User;

import java.util.Optional;

public class Bot extends TelegramLongPollingBot {
    private final String name;
    private final String token;
    private Update update;

    public Bot(String name, String token) {
        this.name = name;
        this.token = token;
    }


    @Override
    public void onUpdateReceived(Update update) {
        this.update = update;
        if (update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> entity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (entity.isEmpty()) {
                return;
            }

            executeCommand(message);
        }
    }

    private void executeCommand(Message message) throws TelegramApiException {
        MainController mainController = new MainController();
        mainController.execute(message,this);
    }

    public void  sendMessage(Message message,String s){
        try {
            execute(SendMessage.builder().text(s)
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

}
