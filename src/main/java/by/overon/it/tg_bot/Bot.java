package by.overon.it.tg_bot;

import by.overon.it.repository.CarsAdsRepository;
import lombok.SneakyThrows;
import org.aspectj.bridge.MessageHandler;
import org.springframework.cache.annotation.Cacheable;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME = "test_bot";
    private final String BOT_TOKEN = "5153744354:AAFufvHy_I6mTRVLQ8slD0ge8s_JA7oF6Og";

    public Bot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }

    @Override
    // Метод, который вызывается при запросе пользователя
    public void onUpdateReceived(Update update) {
        // Получаем текст сообщения
        // Проверяем на содержание "/start" и в случае "true" отправляем ответ пользователю
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            if (message != null) {
                if (message.startsWith("/start")) {
                    try {
                        execute(showGreetingMenu(update.getMessage().getChatId().toString()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().startsWith("1")) {
                try {
                    SendMessage sendMessage = askAboutBrand(update.getCallbackQuery().getMessage().getChatId().toString());
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Метод, отвечающий за вывод приветственного меню. Содержит в себе текст и 3 кнопки
    private SendMessage showGreetingMenu(String chatId) {
        InlineKeyboardButton createAd = new InlineKeyboardButton();  // Создаем кнопку, которая отвечает за создание объявлений
        createAd.setText("Создать объявление");  // Присваиваем кнопке текст
        createAd.setCallbackData("1"); // Устанаваливаем значение, которое придет после нажатия кнопки (Обязательно! Иначе Exception)
        InlineKeyboardButton showAd = new InlineKeyboardButton(); // Создаем кнопку, которая отвечает за показ объявлений
        showAd.setText("Показать объявления");
        showAd.setCallbackData("2");
        InlineKeyboardButton myAd = new InlineKeyboardButton(); // Создаем кнопкуб, которая отвечает за показ моих объявлений
        myAd.setText("Мои объявления");
        myAd.setCallbackData("3");

        // Создаем 2 ряда кнопок
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        // В первом ряду 2 кнопки, а во втором 1
        firstRow.add(createAd);
        firstRow.add(showAd);
        secondRow.add(myAd);

        //Объеденяем кнопки в одно целое для отправки
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>(List.of(
                firstRow,
                secondRow
        ));

        //Создаем и устанавливаем разметку
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(inlineButtons);

        SendMessage sendMessage = createMessage(chatId, "Тест");
        sendMessage.setReplyMarkup(markup); // Устанавливаем разметку
        return sendMessage; // Возвращаем наше сообщение
    }

    public SendMessage createMessage(String chatId, String text) {
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

    public SendMessage askAboutMileage(String chatId) {
        return createMessage(chatId, "Введите пробег автомобиля");
    }

    public SendMessage askAboutYear(String chatId) {
        return createMessage(chatId, "Введите пробег автомобиля");
    }

    public SendMessage askAboutPrice(String chatId) {
        return createMessage(chatId, "Введите цену автомобиля");
    }

    public SendMessage askAboutText(String chatId) {
        return createMessage(chatId, "Введите описание автомобиля");
    }

    public SendMessage askAboutPhoto(String chatId) {
        return createMessage(chatId, "Загрузите фото автомобиля");
    }

    public SendMessage askAboutTelephone(String chatId) {
        return createMessage(chatId, "Введите ваш номер");
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
