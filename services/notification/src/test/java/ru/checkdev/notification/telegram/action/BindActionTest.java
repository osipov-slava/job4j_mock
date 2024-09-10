package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BindActionTest {

    private BindAction bindAction;

    @BeforeEach
    public void initComponents() {
        bindAction = new BindAction(mock(TgAuthCallWebClint.class));
    }

    private Message getMessage(String text) {
        Chat chat = new Chat();
        chat.setId(3543L);
        User user = new User();
        user.setId(985907L);

        Message message = new Message();
        message.setChat(chat);
        message.setFrom(user);
        message.setMessageId(68764);
        message.setText(text);
        return message;
    }

    @Test
    public void whenHandleThenMessage() {
        var message = getMessage("/bind");
        BotApiMethod<Message> expected = new SendMessage(message.getChatId().toString(),
                "Для привязки телеграм аккаунта к учетной записи\nВведите email и пароль(через пробел)");

        var actual = bindAction.handle(message);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenCallbackWithNo2parametersThenMessage() {
        var message = getMessage("first");
        BotApiMethod<Message> expected = new SendMessage(message.getChatId().toString(),
                "Некорректрный ввод, необходимо 2 параметра через пробел\n");

        var actual = bindAction.callback(message);
        assertThat(actual).isEqualTo(expected);

        message.setText("mail password third");
        actual = bindAction.callback(message);
        assertThat(actual).isEqualTo(expected);
    }
}
