//package ru.liga.client.telegram.botapi.handlers;
//
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import ru.liga.client.service.ReplyMessagesService;
//import ru.liga.client.telegram.botapi.BotState;
//import ru.liga.client.telegram.botapi.InputMessageHandler;
//import ru.liga.client.telegram.cache.UserDataCache;
//@Component
//public class StartHandler implements InputMessageHandler {
//    private final UserDataCache userDataCache;
//    private final ReplyMessagesService messagesService;
//
//    public StartHandler(UserDataCache userDataCache,
//                             ReplyMessagesService messagesService) {
//        this.userDataCache = userDataCache;
//        this.messagesService = messagesService;
//    }
//
//    @Override
//    public SendMessage handle(Message message) {
//        return processUsersInput(message);
//    }
//
//    @Override
//    public BotState getHandlerName() {
//        return BotState.START;
//    }
//
//    private SendMessage processUsersInput(Message inputMsg) {
//        long userId = inputMsg.getFrom().getId();
//        long chatId = inputMsg.getChatId();
//
//        SendMessage replyToUser = messagesService.getReplyMessage(String.valueOf(chatId),"reply.start");
//        userDataCache.setUsersCurrentBotState(userId,BotState.FILLING_PROFILE);
//
//        return replyToUser;
//    }
//}
