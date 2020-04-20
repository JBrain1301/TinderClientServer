package ru.jbrain.keyboard;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Data
public class KeyboardSetter {
    @Autowired
    private ReplyKeyboardMarkup markup;
    private static final Logger log = LoggerFactory.getLogger(KeyboardSetter.class);

    public void setStartKeyboard() {
        log.debug("Установка клавиатуры главного окна");
        addSettingsToKeyboard();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add("Влево");
        firstRow.add("Вправо");
        secondRow.add("Анкета");
        secondRow.add("Любимцы");
        rowList.add(firstRow);
        rowList.add(secondRow);
        markup.setKeyboard(rowList);
    }

    public void setProfileKeyboard() {
        log.debug("Установка клавиатуры при вызове команды анкета");
        addSettingsToKeyboard();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();
        firstRow.add("Войти");
        firstRow.add("Регистрация");
        secondRow.add("Изменить описание");
        secondRow.add("Удалить");
        thirdRow.add("Уйти");
        rowList.add(firstRow);
        rowList.add(secondRow);
        rowList.add(thirdRow);
        markup.setKeyboard(rowList);
    }

    private void addSettingsToKeyboard() {
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
    }

}
