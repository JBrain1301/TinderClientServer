package ru.jbrain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.jbrain.bot.Users;
import ru.jbrain.domain.ProfileData;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class CommandExecutor {
    private Map<Long, List<String>> lastProfile = new HashMap<>();
    @Autowired
    private OkHttpClient httpClient;
    private RequestBody requestBody;
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String BORDER = "--------------------------------\n";
    private final String MATCHBORDER = "| |";
    private HttpUrl.Builder urlBuilder;
    private Request request;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private String serverAddress;
    @Autowired
    private ReplyKeyboardMarkup markup;


    public String next(Long id, SendMessage message) {
        setStartKeyboard(markup);
        message.setReplyMarkup(markup);
        urlBuilder = HttpUrl.parse(serverAddress + "/users/next").newBuilder();
        urlBuilder.addQueryParameter("name", Users.getUsersLogged().get(id));
        request = new Request.Builder()
                .url(urlBuilder.build().url())
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String answer = response.body().string();
            System.out.println(answer);
            if (answer.length() > 0) {
                ProfileData profileData = mapper.readValue(answer, ProfileData.class);
                Users.getLastProfile().put(id, profileData);
                String msg = BORDER + profileData.getName() + "\t" + profileData.getDescription() + "\n" + BORDER;
                message.setText(msg);
            } else {
                Users.getLastProfile().put(id,null);
                message.setText("Пользователей нет");
            }
        } catch (IOException e) {
            message.setText("Ошибка");
            e.printStackTrace();
        }
        return null;
    }

    public void profile(Long id, SendMessage message) {
        setProfileKeyboard(markup);
        message.setReplyMarkup(markup);
        message.setText("Выберите....");
    }


    public void loggin(SendMessage message) {
        message.setText(MATCHBORDER + "\t" + "Сударь иль сударыня введите логинъ и пароль черезъ пробѣлъ:" + "\t" + MATCHBORDER);
    }

    public void logginToServer(Long id, SendMessage message, String msg) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", msg.split(" ")[0]);
            jsonObject.put("password", msg.split(" ")[1]);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            requestBody = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url(serverAddress + "/login")
                    .post(requestBody)
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                String answer = response.body().string();
                if (answer.length() > 0) {
                    ProfileData profileData = mapper.readValue(answer, ProfileData.class);
                    Users.getUsersLogged().put(id, profileData.getName());
                    message.setText("Успех");
                } else {
                    message.setText("Неправильные данные");
                }
            } catch (IOException e) {
                message.setText("Ошибка");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            message.setText("Введите логин и пароль через пробел");
        }
    }


    public void register(SendMessage message) {
        message.setText(MATCHBORDER + "Вы сударь иль сударыня? Как вас величать? Ваш секретный шифръ? Какіе вы и что вы ищите? например:" + MATCHBORDER + "\n" +
                "сударь Анархистъ д0л0йцарR *желает отойти от дел в уютной усадьбе с любимой  женщиной*");
    }

    public void registerToServer(Long chatId, SendMessage message, String msg) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gender", msg.split(" ")[0]);
            jsonObject.put("name", msg.split(" ")[1]);
            jsonObject.put("password", msg.split(" ")[2]);
            jsonObject.put("description", msg.substring(msg.indexOf("*")).replaceAll("\\*", ""));
            requestBody = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url(serverAddress + "/login/register")
                    .post(requestBody)
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                String answer = response.body().string();
                if (answer.equalsIgnoreCase("Successful")) {
                    Users.getUsersLogged().put(chatId, msg.split(" ")[1]);
                    message.setText("Успех");
                } else {
                    message.setText(answer);
                }
            } catch (IOException e) {
                message.setText("Ошибка");
            }
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            message.setText("Введите данные как в примере");
        }
    }

    public void changeDescription(Update update, SendMessage message) {
        if (Users.getUsersLogged().get(update.getMessage().getChatId()).equalsIgnoreCase("anonym")) {
            message.setText("Нужно зарегестрироваться или войти в профиль чтобы изменить описание");
        } else {
            message.setText(MATCHBORDER + "\t" + "Какіе вы и что вы ищите?" + "\t" + MATCHBORDER);
        }
    }

    public void changeDescriptionOnServer(Long chatId, SendMessage message, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(chatId));
        jsonObject.put("description", msg);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login/edit")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String answer = response.body().string();
            if (answer.equalsIgnoreCase("Successful")) {
                message.setText("Успех");
            } else {
                message.setText(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void matchs(Long id, SendMessage message) {
        String answer = null;
        urlBuilder = HttpUrl.parse(serverAddress + "/users/match/").newBuilder();
        urlBuilder.addQueryParameter("name", Users.getUsersLogged().get(id));
        request = new Request.Builder()
                .url(urlBuilder.build().url())
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            answer = response.body().string();
        } catch (IOException e) {
            message.setText("Ошибка");
            e.printStackTrace();
        }
        if (answer.length() != 0) {
            try {
                List<ProfileData> myObjects = mapper.readValue(answer, new TypeReference<List<ProfileData>>() {
                });
                if (myObjects.size() > 0) {
                    Users.getLastMatchs().put(id, myObjects);
                    answerParams(message, myObjects);
                } else {
                    message.setText("Любимцев нет");
                }
            } catch (JsonProcessingException e) {
                message.setText("Зарегистрируйтесь или войдите в профиль");
            }
        } else {
            message.setText("Зарегистрируйтесь или войдите в профиль");
        }
    }

    public void addMatch(Long id, SendMessage message) {
        ProfileData profileData = Users.getLastProfile().get(id);
        if (profileData == null) {
            message.setText("Невозможно добавить пользователя");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(id));
        jsonObject.put("matchName", profileData.getName());
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/users/match/add")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String answer = response.body().string();
            message.setText(answer);
            Users.getLastProfile().put(id,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMatch(Long id, SendMessage message, String msg) {
        int number = Integer.parseInt(msg);
        if (number > Users.getLastMatchs().get(id).size() || number <= 0) {
            message.setText("Нет такого любимца");
            return;
        }
        message.setText(Users.getLastMatchs().get(id).get(number - 1).getDescription());
    }

    public void remove(Long id, SendMessage message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(id));
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(serverAddress + "/login/edit/delete")
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String answer = response.body().string();
            if (answer.equalsIgnoreCase("Succesfull")) {
                message.setText(MATCHBORDER + "\t" + "Ваша анкета удалена безвозвратно" + "\t" + MATCHBORDER);
                Users.getUsersLogged().remove(id);
            } else {
                message.setText(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerParams(SendMessage message, List<ProfileData> myObjects) {
        StringBuilder builder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        myObjects.forEach(s -> builder.append(MATCHBORDER).append("\t")
                .append(count.getAndIncrement()).append(".").append("\t")
                .append(s.getName()).append("\t")
                .append(MATCHBORDER).append("\n"));
        message.setText(builder.toString());
    }

    public ReplyKeyboardMarkup getMarkup() {
        return markup;
    }

    public void setMarkup(ReplyKeyboardMarkup markup) {
        this.markup = markup;
    }

    private void setStartKeyboard(ReplyKeyboardMarkup markup) {
        addSettingsToKeyboard(markup);
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

    private void setProfileKeyboard(ReplyKeyboardMarkup markup) {
        addSettingsToKeyboard(markup);
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

    private void addSettingsToKeyboard(ReplyKeyboardMarkup markup) {
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
    }
}
