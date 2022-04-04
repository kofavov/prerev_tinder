package ru.liga.client.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;

@Service
public class ReplyMessagesService {
    private final MessageService messageService;

    public ReplyMessagesService(MessageService messageService) {
        this.messageService = messageService;
    }

    public SendMessage getReplyMessage(String id, String replyMessage){
        return new SendMessage(id,messageService.getMessage(replyMessage));
    }
    public SendMessage getReplyMessage(String id,String... replyMessages){
        StringBuilder concatReply = new StringBuilder();
        Arrays.stream(replyMessages).forEach(replyMessage -> concatReply
                .append(messageService.getMessage(replyMessage))
                .append("\n"));
        return new SendMessage(id,concatReply.toString());
    }
}
