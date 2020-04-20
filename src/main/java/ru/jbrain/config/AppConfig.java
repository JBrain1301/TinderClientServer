package ru.jbrain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.jbrain.bot.Bot;
import ru.jbrain.commands.CommandExecutor;
import ru.jbrain.commands.CommandHandler;
import ru.jbrain.commands.client.RestClient;
import ru.jbrain.keyboard.KeyboardSetter;


@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private String serverPort;

    @Bean
    public MediaType mediaType() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return JSON;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public ReplyKeyboardMarkup ReplyKeyboardMarkup() {
        return new ReplyKeyboardMarkup();
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
    KeyboardSetter keyboardSetter() {
        return new KeyboardSetter();
    }

    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor();
    }
    @Bean
    RestClient restClient() {
        return new RestClient(okHttpClient(),mediaType(),getServerAddress());
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
