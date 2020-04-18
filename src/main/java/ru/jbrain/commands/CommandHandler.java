package ru.jbrain.commands;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.jbrain.bot.Users;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final CommandExecutor executor;
    private Map<Long,String> lastMessage = new HashMap<>();
    @Autowired
    public CommandHandler(CommandExecutor executor) {
        this.executor = executor;
    }

    public void command(Update update, SendMessage message) {
        String msg = update.getMessage().getText();
        if (!Users.getUsersLogged().containsKey(update.getMessage().getChatId())) {
            Users.getUsersLogged().put(update.getMessage().getChatId(),"anonym");
        }
        if (lastMessage.get(update.getMessage().getChatId()) != null && lastMessage.get(update.getMessage().getChatId()).equalsIgnoreCase("войти") && !msg.equalsIgnoreCase("уйти")) {
            executor.logginToServer(update.getMessage().getChatId(),message,msg);
        }
        if (lastMessage.get(update.getMessage().getChatId()) != null && lastMessage.get(update.getMessage().getChatId()).equalsIgnoreCase("любимцы") && NumberUtils.isNumber(msg)) {
            executor.showMatch(update.getMessage().getChatId(),message,msg);
        }
        if (lastMessage.get(update.getMessage().getChatId()) != null && lastMessage.get(update.getMessage().getChatId()).equalsIgnoreCase("регистрация")) {
            executor.registerToServer(update.getMessage().getChatId(),message,msg);
        }
        if (lastMessage.get(update.getMessage().getChatId()) != null && lastMessage.get(update.getMessage().getChatId()).equalsIgnoreCase("Изменить описание")) {
            executor.changeDescriptionOnServer(update.getMessage().getChatId(),message,msg);
        }
        if (msg.equalsIgnoreCase("/start") || msg.equalsIgnoreCase("влево") || msg.equalsIgnoreCase("уйти")) {
            executor.next(update.getMessage().getChatId(),message);
        }
        if (msg.equalsIgnoreCase("вправо")) {
            executor.addMatch(update.getMessage().getChatId(),message);
        }
        if (msg.equalsIgnoreCase("войти")) {
            System.out.println("message");
           executor.loggin(message);
        }
        if (msg.equalsIgnoreCase("регистрация")) {
            executor.register(message);
        }
        if (msg.equalsIgnoreCase("анкета")) {
            executor.profile(update.getMessage().getChatId(),message);
        }
        if (msg.equalsIgnoreCase("любимцы")) {
            executor.matchs(update.getMessage().getChatId(),message);
        }
        if (msg.equalsIgnoreCase("Изменить описание")) {
            executor.changeDescription(update,message);
        }
        if (msg.equalsIgnoreCase("удалить")) {
            executor.remove(update.getMessage().getChatId(),message);
        }
        identifyLastMessage(update,message,msg);
    }

    private void identifyLastMessage(Update update,SendMessage message,String msg) {
        if (message.getText().equalsIgnoreCase("Неправильные данные") || message.getText().equalsIgnoreCase("Введите логин и пароль через пробел")) {
            lastMessage.put(update.getMessage().getChatId(),"Войти");
        } else if (message.getText().equalsIgnoreCase("Нужно зарегестрироваться или войти в профиль чтобы изменить описание")) {
            lastMessage.put(update.getMessage().getChatId(),"/start");
        }else if (message.getText().equalsIgnoreCase("Нет такого любимца")) {
            lastMessage.put(update.getMessage().getChatId(),"любимцы");
        }else if (message.getText().equalsIgnoreCase("Введите данные как в примере") ||message.getText().equalsIgnoreCase("Не правильный пол")   ) {
            lastMessage.put(update.getMessage().getChatId(),"Регистрация");
        }else {
            lastMessage.put(update.getMessage().getChatId(),msg);
        }
    }

    public CommandExecutor getExecutor() {
        return executor;
    }
}
