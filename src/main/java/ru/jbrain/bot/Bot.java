package ru.jbrain.bot;

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

        } catch (TelegramApiValidationException e) {
            System.out.println("Нет данных: " + update.getMessage().getText());
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
