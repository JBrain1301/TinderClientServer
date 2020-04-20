package ru.jbrain.commands;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jbrain.bot.Users;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CommandExecutor executor;
    private final Map<Long, String> lastMessage = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    @Autowired
    public CommandHandler(CommandExecutor executor) {
        this.executor = executor;
    }

    public void command(Update update, SendMessage message) {
        String msg = update.getMessage().getText();
        Long id = update.getMessage().getChatId();
        log.debug("Определение команды из сообщения пользователя: Чат ИД : {}, Сообщение: {} ",id,msg);
        if (!Users.getUsersLogged().containsKey(id)) {
            Users.getUsersLogged().put(id, "anonym");
        }
        checkLastMessage(message, msg, id);
        switch (msg.toLowerCase()) {
            case "/start":
            case "влево":
            case "уйти":
                executor.next(id, message);
                break;
            case "вправо":
                executor.addMatch(id, message);
                break;
            case "войти":
                executor.login(message);
                break;
            case "регистрация":
                executor.register(message);
                break;
            case "анкета":
                executor.profile(message);
                break;
            case "любимцы":
                executor.matchs(id, message);
                break;
            case "изменить описание":
                executor.changeDescription(id, message);
                break;
            case "удалить":
                executor.remove(id, message);
                break;
        }
        identifyLastMessage(id, message, msg);
    }

    private void checkLastMessage(SendMessage message, String msg, Long id) {
        if (lastMessage.get(id) != null && lastMessage.get(id).equalsIgnoreCase("войти") && !msg.equalsIgnoreCase("уйти")) {
            executor.loginToServer(id, message, msg);
        }
        if (lastMessage.get(id) != null && lastMessage.get(id).equalsIgnoreCase("любимцы") && NumberUtils.isNumber(msg)) {
            executor.showMatch(id, message, msg);
        }
        if (lastMessage.get(id) != null && lastMessage.get(id).equalsIgnoreCase("регистрация")) {
            executor.registerToServer(id, message, msg);
        }
        if (lastMessage.get(id) != null && lastMessage.get(id).equalsIgnoreCase("Изменить описание")) {
            executor.changeDescriptionOnServer(id, message, msg);
        }
    }

    private void identifyLastMessage(Long id, SendMessage message, String msg) {
        log.debug("Запись последнего сообщения пользователя исходя из возможных ошибок");
        if (message.getText().equalsIgnoreCase("Неправильные данные") || message.getText().equalsIgnoreCase("Введите логин и пароль через пробел")) {
            lastMessage.put(id, "Войти");
        } else if (message.getText().equalsIgnoreCase("Нужно зарегестрироваться или войти в профиль чтобы изменить описание")) {
            lastMessage.put(id, "/start");
        } else if (message.getText().equalsIgnoreCase("Нет такого любимца")) {
            lastMessage.put(id, "любимцы");
        } else if (message.getText().equalsIgnoreCase("Введите данные как в примере") || message.getText().equalsIgnoreCase("Не правильный пол")) {
            lastMessage.put(id, "Регистрация");
        } else {
            lastMessage.put(id, msg);
        }
    }

    public CommandExecutor getExecutor() {
        return executor;
    }
}
