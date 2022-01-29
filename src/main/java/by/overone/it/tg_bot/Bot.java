package by.overone.it.tg_bot;

import by.overone.it.dao.BotStatusService;
import by.overone.it.entity.BotStatus;
import by.overone.it.enums.BotStatusEnums;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    @Autowired
    private BotStatusService botStatusService;
    private BotStatus botStatus;
    private final String BOT_NAME = "test_bot";
    private final String BOT_TOKEN = "5153744354:AAFufvHy_I6mTRVLQ8slD0ge8s_JA7oF6Og";

    @Override
    @SneakyThrows
    // Метод, который вызывается при запросе пользователя
    public void onUpdateReceived(Update update) {
        // Проверяем на наличие сообщения
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            if (message != null) {
                String chatId = update.getMessage().getChatId().toString();
                // Проверяем на содержание "/start" и в случае "true" отправляем ответ пользователю
                if (message.startsWith("/start")) {
                    // Отправляем приветственное меню
                    showGreetingMenu(chatId);
                } else if (botStatus != null && update.getMessage().hasText()) {
                    botStatus = botStatusService.findFirstByChatId(chatId);

                    // Начало цепочки вопрос-ответ
                    if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_MODEL.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_YEAR.toString());
                        askAboutModel(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_YEAR.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_MILEAGE.toString());
                        askAboutYear(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_MILEAGE.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_PRICE.toString());
                        askAboutMileage(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_PRICE.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_PHOTO.toString());
                        askAboutPrice(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_PHOTO.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_DESCRIPTION.toString());
                        askAboutPhoto(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_DESCRIPTION.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_TELEPHONE.toString());
                        askAboutDescription(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_TELEPHONE.toString())) {
                        warnAboutCreationAd(chatId);
                    }
                }
            }
        }
        // Проверяем на наличие нажатой кнопки
        else if (update.hasCallbackQuery()) {
            // Проверяем полученное значение кнопки
            if (update.getCallbackQuery().getData().startsWith("1")) {
                // Создаем объект статуса бота
                botStatus = new BotStatus();
                // Считываем id чата
                String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                // Устанавливаем значение chatId нашему объекту
                botStatus.setChatId(chatId);
                // Устанавливаем состояние бота
                botStatus.setStatus(BotStatusEnums.ASK_ABOUT_MODEL.toString());
                // Сохраняем в бд
                botStatus = botStatusService.save(botStatus);
                // Отправляем вопрос
                askAboutBrand(chatId);
            }
        }
    }

    @SneakyThrows
    // Метод, отвечающий за вывод приветственного меню. Содержит в себе текст и 3 кнопки
    private void showGreetingMenu(String chatId) {
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
        execute(sendMessage);
    }

    // Приватный метод для создания объекта SendMessage
    private SendMessage createMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @SneakyThrows
    public void askAboutBrand(String chatId) {
        execute(createMessage(chatId, "Введите бренд автомобиля"));
    }

    @SneakyThrows
    public void askAboutModel(String chatId) {
        execute(createMessage(chatId, "Введите модель"));
    }

    @SneakyThrows
    public void askAboutMileage(String chatId) {
        execute(createMessage(chatId, "Введите пробег автомобиля"));
    }

    @SneakyThrows
    public void askAboutYear(String chatId) {
        execute(createMessage(chatId, "Введите год автомобиля"));
    }

    @SneakyThrows
    public void askAboutPrice(String chatId) {
        execute(createMessage(chatId, "Введите цену автомобиля"));
    }

    @SneakyThrows
    public void askAboutDescription(String chatId) {
        execute(createMessage(chatId, "Введите описание автомобиля"));
    }

    @SneakyThrows
    public void askAboutPhoto(String chatId) {
        execute(createMessage(chatId, "Загрузите фото автомобиля"));
    }

    @SneakyThrows
    public void askAboutTelephone(String chatId) {
        execute(createMessage(chatId, "Введите ваш номер"));
    }

    @SneakyThrows
    public void warnAboutCreationAd(String chatId) {
        execute(createMessage(chatId, "Объявление успешно создано и отправлено на рассмотрение администратору"));
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
