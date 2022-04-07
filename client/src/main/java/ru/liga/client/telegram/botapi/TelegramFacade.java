package ru.liga.client.telegram.botapi;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.client.controller.ServerController;
import ru.liga.client.entity.User;
import ru.liga.client.telegram.Bot;
import ru.liga.client.telegram.cache.UserDataCache;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final ServerController serverController;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, ServerController serverController) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.serverController = serverController;
    }

    public BotApiMethod<?> handleUpdate(Update update, Bot bot) {
        log.info(update.getUpdateId().toString());
        BotApiMethod<?> replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("CallbackQuery from User {} CQ {}"
                    , callbackQuery.getFrom().getId(), callbackQuery.getData());

            processCallBackQuery(callbackQuery, bot);
        }

        replyMessage = handleInputMessage(update, bot);

        return replyMessage;
    }

    private BotApiMethod<?> processCallBackQuery(CallbackQuery buttonQuery, Bot bot) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;

        //choose buttons
        switch (buttonQuery.getData()) {
            case "buttonMan":
                buttonsChooseProfileGender(userId, "Сударъ");
                break;
            case "buttonWoman":
                buttonsChooseProfileGender(userId, "Сударыня");
                break;
            case "buttonProfile":
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_PROFILE);
                break;
            case "buttonSearch":
                userDataCache.setUsersCurrentBotState(userId, BotState.SEARCH);
                break;
            case "buttonSearchMan":
                buttonSearchChooseGender(userId, "Сударъ");
                break;
            case "buttonSearchWoman":
                buttonSearchChooseGender(userId, "Сударыня");
                break;
            case "buttonSearchAll":
                buttonSearchAll(userId);
                break;
            case "buttonDislike":
                buttonDislike(userId);
                break;
            case "buttonLike":
                buttonLike(bot, userId);
                break;
            case "buttonMenu":
                buttonMenu(userId);
                break;
            case "buttonChooseLovers":
                userDataCache.setUsersCurrentBotState(userId, BotState.LOVERS);
                break;
            case "buttonLovers":
                buttonLovers(userId);
                break;
            case "buttonLoved":
                buttonLoved(userId);
                break;
            case "buttonMatch":
                buttonMatch(userId);
                break;
            case "buttonBack":
                buttonBack(userId);
                break;
            case "buttonNext":
                buttonNext(userId);
                break;
            default:
                userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
                break;
        }

        return callBackAnswer;
    }

    private void buttonsChooseProfileGender(long userId, String gender) {
        User user = userDataCache.getUserProfileData(userId);
        user.setGender(gender);
        userDataCache.saveUserProfileData(userId, user);
        userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HEAD);
    }

    private void buttonSearchChooseGender(long userId, String gender) {
        removeCacheSearch(userId);
        userDataCache.fillUsersProfilesForSearch(userId,
                serverController.getAllWithFilter(u -> u.getGender().equals(gender)));
        userDataCache.setUsersCurrentBotState(userId, BotState.CHOSEN_LOVERS_GENDER);
    }

    private void buttonSearchAll(long userId) {
        removeCacheSearch(userId);
        userDataCache.fillUsersProfilesForSearch(userId,
                serverController.getAllWithFilter(u -> u.getGender().equals("Сударъ")
                        || u.getGender().equals("Сударыня")));
        userDataCache.setUsersCurrentBotState(userId, BotState.CHOSEN_LOVERS_GENDER);
    }

    private void buttonDislike(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT);
        User user = userDataCache.getUserProfileData(userId);
        Long lastProfile = userDataCache.getLastSearchIdForUser(userId);
        try {
            user.getLovers().remove(lastProfile);
        } catch (Exception e) {
            log.info("Возлюбленный {} юзера {} не удален из кэша", lastProfile, userId);
        }
        serverController.removeLover(userId, lastProfile);

        newLastSearchId(userId, lastProfile);
    }

    private void buttonLike(Bot bot, long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT);
        User user = userDataCache.getUserProfileData(userId);
        Long lastProfileId = userDataCache.getLastSearchIdForUser(userId);
        try {
            User lover = userDataCache.getUserProfilesForSearch(userId).get(lastProfileId);
            user.getLovers().put(lastProfileId, lover);
            serverController.addNewLover(userId, lastProfileId);
        } catch (Exception e) {
            log.info("Ошибка при добавлении Возлюбленного {} юзера {} в список возлюбленных",
                    userId, lastProfileId);
        }
        checkSypathy(bot, userId, lastProfileId);
        newLastSearchId(userId, lastProfileId);
    }

    private void checkSypathy(Bot bot, long userId, Long lastProfileId) {
        TreeMap<Long, User> searchUsers =
                userDataCache.getUserProfilesForSearch(userId);
        User currentSearchProfile = searchUsers.get(lastProfileId);
        serverController.fillLoversMap(lastProfileId, currentSearchProfile);
        currentSearchProfile.setLovers(currentSearchProfile.getLovers());
        if (currentSearchProfile.getLovers().containsKey(userId)) {
            bot.sendMessage(new SendMessage(String.valueOf(userId), "Вы любимы!"));
        }
    }

    private void buttonNext(long userId) {
        userDataCache.setUsersCurrentBotState(userId, BotState.NEXT_LOVERS);
        Long lastLoverId = userDataCache.getLastLoverId(userId);
        newNextLastLoverId(userId,lastLoverId);
    }
    private void buttonBack(long userId){
        userDataCache.setUsersCurrentBotState(userId, BotState.BACK_LOVERS);
        Long lastLoverId = userDataCache.getLastLoverId(userId);
        newBackLastLoverId(userId,lastLoverId);
    }

    private void buttonMenu(long userId) {
        removeCacheSearch(userId);
        userDataCache.setUsersCurrentBotState(userId, BotState.PRE_SEARCH);
    }

    private void buttonLovers(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId,user);
        userDataCache.fillUserLoversData(userId,user.getLovers());
        userDataCache.setUsersCurrentBotState(userId,BotState.NEXT_LOVERS);
    }

    private void buttonLoved(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId,user);
        userDataCache.fillUserLoversData(userId,user.getLoved());
        userDataCache.setUsersCurrentBotState(userId,BotState.NEXT_LOVERS);
    }

    private void buttonMatch(long userId) {
        removeCacheLovers(userId);
        User user = serverController.getUserById(userId);
        userDataCache.saveUserProfileData(userId,user);
        Map<Long,User> lovers = user.getLovers();
        Map<Long,User> loved = user.getLoved();
        Map<Long,User> match = Maps.filterKeys(lovers, loved::containsKey);
        userDataCache.fillUserLoversData(userId,match);
        userDataCache.setUsersCurrentBotState(userId,BotState.NEXT_LOVERS);
    }

    private void newLastSearchId(long userId, Long lastProfile) {
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserProfilesForSearch(userId).higherKey(lastProfile);
            userDataCache.setUsersLastElementForSearch(userId, nextProfile);
        }
    }
    private void newNextLastLoverId(long userId, Long lastProfile){
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserLoversData(userId).higherKey(lastProfile);
            userDataCache.setUsersLastLover(userId, nextProfile);
        }
    }
    private void newBackLastLoverId(long userId, Long lastProfile){
        if (lastProfile != null) {
            Long nextProfile = userDataCache.getUserLoversData(userId).lowerKey(lastProfile);
            if(nextProfile == null) {
                nextProfile = userDataCache.getUserLoversData(userId).lastKey();
            }
            userDataCache.setUsersLastLover(userId, nextProfile);
        }
//        else {
//            Long nextProfile = userDataCache.getUserLoversData(userId).lastKey();
//            userDataCache.setUsersLastLover(userId, nextProfile);
//        }
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


    private BotApiMethod<?> handleInputMessage(Update update, Bot bot) {
        long userId;//todo заменить try catch на if null?
        try {
            userId = getUserId(update);
        } catch (NullPointerException e) {
            return null;
        }

        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        BotApiMethod<?> replyMessage;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String inputMessage = message.getText();
            log.info("Message from User {} Text {}", message.getFrom().getId(), message.getText());
            botState = chooseBotStateFromInputText(userId, inputMessage);
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        try {
            replyMessage = botStateContext.processInputMessage(botState, update, userId, bot);
        } catch (Exception e) {
            userDataCache.removeUser(userId);
            replyMessage = new SendMessage(String.valueOf(userId), "ошибка введите /start");
            log.info("не удалось обработать запрос пользователя {}", userId);
        }

        return replyMessage;
    }

    private BotState chooseBotStateFromInputText(long userId, String inputMessage) {
        BotState botState;
        switch (inputMessage) {
            case "/start":
                botState = BotState.START;
                break;
            case "/name":
                botState = BotState.ASK_NAME;
                break;
            case "/gender":
                botState = BotState.ASK_GENDER;
                break;
            case "/head":
                botState = BotState.ASK_HEAD;
                break;
            case "/desc":
                botState = BotState.ASK_DESC;
                break;
            case "/profile":
                botState = BotState.SHOW_PROFILE;
                break;
            case "/search":
                botState = BotState.SEARCH;
                removeCacheSearch(userId);
                break;
            case "/lovers":
                botState = BotState.LOVERS;
                removeCacheLovers(userId);
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }
        return botState;
    }

    private void removeCacheLovers(long userId) {
        try {
            userDataCache.getUserLoversData(userId).remove(userId);
            userDataCache.removeLastLoverProfile(userId);
        } catch (NullPointerException ignored) {
        }
    }

    private void removeCacheSearch(long userId) {
        try {
            userDataCache.getUserProfilesForSearch(userId).remove(userId);
            userDataCache.removeLastSearchProfile(userId);
        } catch (NullPointerException ignored) {
        }
    }

    private Long getUserId(Update update) {
        Long id = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            id = message.getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId();
        }
        if (id == null)
            log.info("Не удалось получить userId update {}", update.getUpdateId());
        return id;
    }
}
