package ru.liga.client.telegram.botapi.handlers.helper;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonHelper {
    public static InlineKeyboardMarkup getInlineKeyboardMarkup(InlineKeyboardMarkup inlineKeyboardMarkup,
                                                         InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>(Arrays.asList(buttons));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
