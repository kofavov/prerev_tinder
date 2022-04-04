package ru.liga.client.telegram.botapi;

public enum BotState {
    START,
    ASK_NAME,
    ASK_GENDER,
    ASK_HEAD,
    ASK_DESC,
    FILLING_PROFILE,
    SHOW_PROFILE,
    FILLED_PROFILE
}
