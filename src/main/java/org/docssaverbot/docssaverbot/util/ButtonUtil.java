package org.docssaverbot.docssaverbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ButtonUtil {

    /**
     * ReplyKeyboardMarkup
     */

    public static KeyboardButton button(String text){
        return new KeyboardButton(text);
    }

    public static KeyboardButton button(String text,String emoji){
        return new KeyboardButton(emoji+" "+text);
    }

    public static KeyboardRow row(KeyboardButton button){
        KeyboardRow row=new KeyboardRow();
        row.add(button);
        return row;
    }

    public static KeyboardRow row(String text) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(text));
        return row;
    }

    public static KeyboardRow row(String text, String emoji) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(emoji + " " + text));
        return row;
    }

    public static List<KeyboardRow> rows(KeyboardRow... row) {
        List<KeyboardRow> rows = new ArrayList<>();
        rows.addAll(Arrays.asList(row));
        return rows;
    }

    public static ReplyKeyboardMarkup keyboard(List<KeyboardRow> rows) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(rows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setSelective(true);
        return keyboard;
    }

    public static ReplyKeyboardMarkup crudOperation(){
        KeyboardRow rowFile = ButtonUtil.row(ButtonUtil.button("add file", "➕"));
        rowFile.add(ButtonUtil.button("view files", "\uD83D\uDCC4"));

        KeyboardRow rowPackage = ButtonUtil.row(ButtonUtil.button("edit folder", "✏\uFE0F"));
        rowPackage.add(ButtonUtil.button("delete folder", "\uD83D\uDDD1"));

        KeyboardRow rowBack = ButtonUtil.row("Back", "⬅\uFE0F");

        return ButtonUtil.keyboard(ButtonUtil.rows(rowFile, rowPackage, rowBack));
    }

    /**
     * InlineKeyboardMarkup
     */

    public static InlineKeyboardButton buttonInline(String text,String callbackData){
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static InlineKeyboardButton buttonInline(String text,String callbackData,String emoji){
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(emoji+" "+text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static List<InlineKeyboardButton> rowInline(InlineKeyboardButton... inlineKeyboardButtons){
        List<InlineKeyboardButton> row=new LinkedList<>();
        row.addAll(Arrays.asList(inlineKeyboardButtons));
        return row;
    }

    public static List<List<InlineKeyboardButton>> collection(List<InlineKeyboardButton>... rows){
        List<List<InlineKeyboardButton>> collection=new LinkedList<>();
        collection.addAll(Arrays.asList(rows));
        return collection;
    }

    public static InlineKeyboardMarkup keyboardInline(List<List<InlineKeyboardButton>> collection){
        InlineKeyboardMarkup keyboardMarkup=new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(collection);
        return keyboardMarkup;
    }
}
