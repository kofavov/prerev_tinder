package ru.liga.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.liga.client.controller.ServerController;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.botapi.telegramfacade.TelegramFacade;

@Setter
@Getter
@Configuration
public class Config {
    @Value("${telegram.name}")
    private String name;
    @Value("${telegram.token}")
    private String token;
    @Value("${telegram.webHookPath}")
    private String webHookPath;
    @Value("${backend.url}")
    private String urlResource;

    @Bean
    public Bot bot(TelegramFacade telegramFacade) {
        return new Bot(name, token, webHookPath,telegramFacade);
    }
    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    @Bean
    public ServerController serverController(){
        return new ServerController(urlResource);
    }

}
