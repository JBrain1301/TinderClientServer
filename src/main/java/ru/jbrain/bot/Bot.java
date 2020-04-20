package ru.jbrain.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.jbrain.commands.CommandHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

public class Bot extends TelegramLongPollingBot {
    private final CommandHandler handler;
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    @Autowired
    public Bot(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        try {
            handler.command(update,message);
            Message execute = execute(message);
            if (execute.getText().equalsIgnoreCase("успех") || execute.getText().contains("Ваша анкета удалена безвозвратно")) {
                handler.getExecutor().next(update.getMessage().getChatId(),message);
                execute(message);
            }
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "@JBrainTestBot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

}
