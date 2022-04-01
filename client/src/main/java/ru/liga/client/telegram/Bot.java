package ru.liga.client.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.client.controller.MainController;

import java.util.Optional;

public class Bot extends TelegramLongPollingBot {
    private final String name;
    private final String token;

    public Bot(String name, String token) {
        this.name = name;
        this.token = token;
    }


    @Override
    public void onUpdateReceived(Update update) {
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
            String command = message.getText();
            executeCommand(message, command);
        }
    }

    private void executeCommand(Message message, String command) throws TelegramApiException {
        MainController mainController = new MainController();
        if (command.equals("/all"))
            execute(SendMessage.builder().text(mainController.getAllUsers())
                    .chatId(message.getChatId().toString())
                    .build());
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
