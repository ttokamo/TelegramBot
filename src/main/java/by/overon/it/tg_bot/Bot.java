package by.overon.it.tg_bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME = "test_bot";
    private final String BOT_TOKEN = "5153744354:AAFufvHy_I6mTRVLQ8slD0ge8s_JA7oF6Og";

    public Bot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }

    @Override
    @SneakyThrows
    // Метод, который вызывается при запросе пользователя
    public void onUpdateReceived(Update update) {
        // Проверяем на наличие сообщения
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            if (message != null) {
                // Проверяем на содержание "/start" и в случае "true" отправляем ответ пользователю
                if (message.startsWith("/start")) {
                    // Отправляем приветственное меню
                    execute(showGreetingMenu(update.getMessage().getChatId().toString()));
                }
            }
        }
        // Проверяем на наличие нажатой кнопки
        else if (update.hasCallbackQuery()) {
            // Проверяем полученное значение кнопки
            if (update.getCallbackQuery().getData().startsWith("1")) {
                // Отправляем сообщение в зависимости от значения кнопки
                execute(askAboutBrand(update.getCallbackQuery().getMessage().getChatId().toString()));
            }
        }
    }

    // Метод, отвечающий за вывод приветственного меню. Содержит в себе текст и 3 кнопки
    private SendMessage showGreetingMenu(String chatId) {
        // Создаем кнопку, которая отвечает за создание объявлений
        InlineKeyboardButton createAd = new InlineKeyboardButton();
        // Присваиваем кнопке текст
        createAd.setText("Создать объявление");
        // Устанаваливаем значение, которое придет после нажатия кнопки (Обязательно! Иначе Exception)
        createAd.setCallbackData("1");
        // Создаем кнопку, которая отвечает за показ всех объявлений
        InlineKeyboardButton showAd = new InlineKeyboardButton();
        showAd.setText("Показать объявления");
        showAd.setCallbackData("2");
        // Создаем кнопку, которая отвечает за показ собственных сообщений объявлений
        InlineKeyboardButton myAd = new InlineKeyboardButton();
        myAd.setText("Мои объявления");
        myAd.setCallbackData("3");

        // Создаем 2 ряда кнопок
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        // Раскидываем кнопки по рядам. В данном случае в первом ряду 2 кнопки, а во втором 1
        firstRow.add(createAd);
        firstRow.add(showAd);
        secondRow.add(myAd);

        // Объеденяем кнопки в одно целое для отправки
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>(List.of(
                firstRow,
                secondRow
        ));

        // Создаем и устанавливаем разметку для кнопок
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(inlineButtons);

        SendMessage sendMessage = createMessage(chatId, "Тест");
        // Устанавливаем наш тип кнопки для сообщения
        sendMessage.setReplyMarkup(markup);
        // Возвращаем наше сообщение с кнопками
        return sendMessage;
    }

    // Приватный метод для создания объекта SendMessage
    private SendMessage createMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }


    public SendMessage askAboutBrand(String chatId) {
        return createMessage(chatId, "Введите бренд автомобиля");
    }

    public SendMessage askAboutModel(String chatId) {
        return createMessage(chatId, "Введите модель");
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
