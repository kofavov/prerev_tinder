package ru.liga.client.telegram.botapi;

public enum BotState {
    START,
    ASK_NAME,
    ASK_GENDER,
    ASK_HEAD,
    ASK_DESC,
    FILLING_PROFILE,
    FILLED_PROFILE,
    PRE_SEARCH,
    SHOW_PROFILE,
    SEARCH,
    CHOOSE_LOVERS_GENDER,
    CHOSEN_LOVERS_GENDER,
    NEXT,
    BACK,
    LOVERS,
    CHOSEN_IN_LOVERS_MENU,
    NEXT_LOVERS,
    BACK_LOVERS
}
