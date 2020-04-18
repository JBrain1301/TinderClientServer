package ru.jbrain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.jbrain.bot.Bot;
import ru.jbrain.commands.CommandExecutor;
import ru.jbrain.commands.CommandHandler;


@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
    @Bean
    public ReplyKeyboardMarkup ReplyKeyboardMarkup() {
        return new ReplyKeyboardMarkup();
    }

    @Bean
    public JSONObject jsonObject() {
        return new JSONObject();
    }

    @Bean
    public ObjectMapper ObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public String getServerAddress() {
        return "http://" + serverAddress + ":" + serverPort;
    }

    @Bean
    public CommandHandler commandHandler() {
        return new CommandHandler(commandExecutor());
    }

    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor();
    }

    @Bean
    public Bot bot() {
        return new Bot(commandHandler());
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }
}
