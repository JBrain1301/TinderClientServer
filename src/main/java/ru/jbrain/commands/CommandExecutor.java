package ru.jbrain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.jbrain.bot.Users;
import ru.jbrain.commands.client.RestClient;
import ru.jbrain.domain.ProfileData;
import ru.jbrain.keyboard.KeyboardSetter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    private final String BORDER = "--------------------------------\n";
    private final String MATCHBORDER = "| |";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private KeyboardSetter keyboardSetter;
    @Autowired
    private RestClient client;


    public void next(Long id, SendMessage message) {
        logger.debug("{} id : Команда на получение следующего пользователя",id);
        keyboardSetter.setStartKeyboard();
        message.setReplyMarkup(keyboardSetter.getMarkup());
        String answer = client.nextRequest(id);
        if (answer.length() > 0) {
            try {
                ProfileData profileData = mapper.readValue(answer, ProfileData.class);
                Users.getLastProfile().put(id, profileData);
                String msg = BORDER + profileData.getName() + "\t" + profileData.getDescription() + "\n" + BORDER;
                message.setText(msg);
            } catch (JsonProcessingException e) {
                message.setText("Ошибка");
                logger.error("{} id : Ошибка получения ProfileData из ответа сервера",id);
            }
        } else {
            Users.getLastProfile().put(id, null);
            message.setText("Пользователей нет");
        }
    }

    public void profile(SendMessage message) {
        logger.debug("Установка клавиатуры для анкеты");
        keyboardSetter.setProfileKeyboard();
        message.setReplyMarkup(keyboardSetter.getMarkup());
        message.setText("Выберите....");
    }


    public void login(SendMessage message) {
        logger.debug("Установка сообщения для логина пользователя");
        message.setText(MATCHBORDER + "\t" + "Сударь иль сударыня введите логинъ и пароль черезъ пробѣлъ:" + "\t" + MATCHBORDER);
    }

    public void loginToServer(Long id, SendMessage message, String msg) {
        logger.debug("{} id : Команда для логина пользователя на сервере",id);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", msg.split(" ")[0]);
            jsonObject.put("password", msg.split(" ")[1]);
            String answer = client.loginRequest(jsonObject.toString());
            if (answer.length() > 0) {
                ProfileData profileData = mapper.readValue(answer, ProfileData.class);
                Users.getUsersLogged().put(id, profileData.getName());
                message.setText("Успех");
            } else {
                message.setText("Неправильные данные");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("{} id : Данные введены не правильно",id);
            message.setText("Введите логин и пароль через пробел");
        } catch (JsonProcessingException e) {
            logger.error("{} id : Ошибка получения ProfileData из ответа",id);
            message.setText("Ошибка");

        }
    }


    public void register(SendMessage message) {
        logger.debug("Команда на установку сообщения для регистрации пользователя");
        message.setText(MATCHBORDER + "Вы сударь иль сударыня? Как вас величать? Ваш секретный шифръ? Какіе вы и что вы ищите? например:" + MATCHBORDER + "\n" +
                "сударь Анархистъ д0л0йцарR *желает отойти от дел в уютной усадьбе с любимой  женщиной*");
    }

    public void registerToServer(Long id, SendMessage message, String msg) {
        logger.debug("{} id : Команда на регистрацию пользователя на сервере",id);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gender", msg.split(" ")[0]);
            jsonObject.put("name", msg.split(" ")[1]);
            jsonObject.put("password", msg.split(" ")[2]);
            jsonObject.put("description", msg.substring(msg.indexOf("*")).replaceAll("\\*", ""));
            String answer = client.registerRequest(jsonObject.toString());
            if (answer.equalsIgnoreCase("Успех")) {
                Users.getUsersLogged().put(id, msg.split(" ")[1]);
            }
            message.setText(answer);
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            logger.error("{} id : Данные введены не правильно",id);
            message.setText("Введите данные как в примере");
        }
    }

    public void changeDescription(Long id, SendMessage message) {
        logger.debug("{} id : Команда на установку сообщения для изменеия описания пользователя",id);
        if (Users.getUsersLogged().get(id).equalsIgnoreCase("anonym")) {
            message.setText("Нужно зарегестрироваться или войти в профиль чтобы изменить описание");
        } else {
            message.setText(MATCHBORDER + "\t" + "Какіе вы и что вы ищите?" + "\t" + MATCHBORDER);
        }
    }

    public void changeDescriptionOnServer(Long id, SendMessage message, String msg) {
        logger.debug("{} id : Команда на измение описания пользователя",id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(id));
        jsonObject.put("description", msg);
        String answer = client.changeDescriptionRequest(jsonObject.toString());
        message.setText(answer);
    }

    public void matchs(Long id, SendMessage message) {
        logger.debug("{} id : Команда на получение матчей пользователя",id);
        String answer = client.mathcsRequest(id);
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
                logger.error("{} id : Ошибка поулчения ProfileData из ответа",id);
                message.setText("Зарегистрируйтесь или войдите в профиль");
            }
        } else {
            message.setText("Зарегистрируйтесь или войдите в профиль");
        }
    }

    public void addMatch(Long id, SendMessage message) {
        logger.debug("{} id : Команда на добавление матча пользователя",id);
        ProfileData profileData = Users.getLastProfile().get(id);
        if (profileData == null) {
            message.setText("Невозможно добавить пользователя");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(id));
        jsonObject.put("matchName", profileData.getName());
        String answer = client.addMatchRequest(jsonObject.toString());
        message.setText(answer);
        Users.getLastProfile().put(id, null);
    }

    public void showMatch(Long id, SendMessage message, String msg) {
        logger.debug("{} id : Команда для показа пользователя из списка матчей пользователя",id);
        int number = Integer.parseInt(msg);
        if (number > Users.getLastMatchs().get(id).size() || number <= 0) {
            message.setText("Нет такого любимца");
            return;
        }
        message.setText(Users.getLastMatchs().get(id).get(number - 1).getDescription());
    }

    public void remove(Long id, SendMessage message) {
        logger.debug("{} id : Команда на удаление пользователя",id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", Users.getUsersLogged().get(id));
        String answer = client.removeRequest(jsonObject.toString());
        if (answer.equalsIgnoreCase("Успех")) {
            message.setText(MATCHBORDER + "\t" + "Ваша анкета удалена безвозвратно" + "\t" + MATCHBORDER);
            Users.getUsersLogged().remove(id);
        } else {
            message.setText(answer);
        }
    }


    private void answerParams(SendMessage message, List<ProfileData> myObjects) {
        logger.debug("Шаблон для сообщения на вывод матчей пользователя");
        StringBuilder builder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        myObjects.forEach(s -> builder.append(MATCHBORDER).append("\t")
                .append(count.getAndIncrement()).append(".").append("\t")
                .append(s.getName()).append("\t")
                .append(MATCHBORDER).append("\n"));
        message.setText(builder.toString());
    }
}
