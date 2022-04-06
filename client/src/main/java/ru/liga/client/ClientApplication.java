package ru.liga.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.starter.TelegramBotInitializer;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//import ru.liga.client.telegram.Bot;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class);

    }
}
