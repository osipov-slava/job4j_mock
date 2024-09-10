package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

@AllArgsConstructor
@Slf4j
public class BindAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_BIND = "/telegram/bind";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Для привязки телеграм аккаунта к учетной записи\nВведите email и пароль(через пробел)";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        String[] args = message.getText().split(" ");
        var text = "";
        if (args.length != 2) {
            text = "Некорректрный ввод, необходимо 2 параметра через пробел\n";
            return new SendMessage(chatId, text);
        }
        var email = args[0];
        var password = args[1];
        var sl = System.lineSeparator();

        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " не корректный." + sl
                   + "попробуйте снова." + sl
                   + "/bind";
            return new SendMessage(chatId, text);
        }

        var person = new PersonDTO(email, password, message.getFrom().getId(), true, null, null);
        Object result;
        try {
            result = authCallWebClint.doPost(URL_BIND, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                   + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка /bind: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        text = "Ваш телеграм аккаунт привязан " + sl
               + "к email: " + email;
        return new SendMessage(chatId, text);
    }
}
