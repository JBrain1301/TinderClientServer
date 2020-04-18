package ru;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.jbrain.bot.Bot;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.jbrain.config.AppConfig;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TelegramBotsApi telegramBot = context.getBean("telegramBotsApi", TelegramBotsApi.class);
        try {
            telegramBot.registerBot(context.getBean("bot",Bot.class));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

}
