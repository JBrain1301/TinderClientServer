package ru.jbrain.commands.client;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.jbrain.bot.Users;
import ru.jbrain.commands.client.api.Client;
import java.io.IOException;

public class RestClient implements Client {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);
    private OkHttpClient httpClient;
    private RequestBody requestBody;
    private MediaType JSON;
    private HttpUrl.Builder urlBuilder;
    private Request request;
    private String serverAddress;

    @Autowired
    public RestClient(OkHttpClient httpClient, MediaType JSON, String serverAddress) {
        this.httpClient = httpClient;
        this.JSON = JSON;
        this.serverAddress = serverAddress;
    }

    public String nextRequest(Long id) {
        log.debug("Отправка запроса для получение следующего пользователя");
        urlBuilder = HttpUrl.parse(serverAddress + "/users/next").newBuilder();
        urlBuilder.addQueryParameter("name", Users.getUsersLogged().get(id));
        request = new Request.Builder()
                .url(urlBuilder.build().url())
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String loginRequest(String jsonObject) {
        log.debug("Отправка запроса для логина пользователя");
        requestBody = RequestBody.create(jsonObject, JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String registerRequest(String jsonObject) {
        log.debug("Отправка запроса на регистрацию пользователя");
        requestBody = RequestBody.create(jsonObject, JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login/register")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String changeDescriptionRequest(String jsonObject) {
        log.debug("Отправка запроса на изменение описания пользователя");
        RequestBody requestBody = RequestBody.create(jsonObject, JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login/edit")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String mathcsRequest(Long id) {
        log.debug("Отправка запроса на получение матчей пользователя");
        urlBuilder = HttpUrl.parse(serverAddress + "/users/match/").newBuilder();
        urlBuilder.addQueryParameter("name", Users.getUsersLogged().get(id));
        request = new Request.Builder()
                .url(urlBuilder.build().url())
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String addMatchRequest(String jsonObject) {
        log.debug("Отправка запроса на добавление матча пользователя");
        requestBody = RequestBody.create(jsonObject, JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/users/match/add")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }

    public String removeRequest(String jsonObject) {
        log.debug("Отправка запроса на удаление пользователя");
        requestBody = RequestBody.create(jsonObject, JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login/edit/delete")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            log.error("Ошибка отправления запроса");
        }
        return "";
    }
}