package by.overone.it.tg_bot;

import by.overone.it.dao.AdService;
import by.overone.it.dao.BotStatusService;
import by.overone.it.entity.Ad;
import by.overone.it.entity.BotStatus;
import by.overone.it.enums.BotStatusEnums;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
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
    @Autowired
    private AdService adService;
    private Ad ad;
    private BotStatus botStatus;
    private final String BOT_NAME = "test_bot";
    private final String BOT_TOKEN = "5153744354:AAFufvHy_I6mTRVLQ8slD0ge8s_JA7oF6Og";

    @Override
    @SneakyThrows
    // Метод, который вызывается при запросе пользователя
    public synchronized void onUpdateReceived(Update update) {
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
                    String id = ad.getId();

                    botStatus = botStatusService.findFirstByChatId(chatId);
                    // Начало цепочки вопрос-ответ
                    if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_MODEL.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_YEAR.toString());
                        adService.updateBrand(id, message);
                        askAboutModel(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_YEAR.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_MILEAGE.toString());
                        adService.updateModel(id, message);
                        askAboutYear(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_MILEAGE.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_PRICE.toString());
                        adService.updateYear(id, message);
                        askAboutMileage(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_PRICE.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_PHOTO.toString());
                        adService.updateMileage(id, message);
                        askAboutPrice(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_PHOTO.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_DESCRIPTION.toString());
                        adService.updatePrice(id, message);
                        askAboutPhoto(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_DESCRIPTION.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.ASK_ABOUT_TELEPHONE.toString());
                        askAboutDescription(chatId);

                    } else if (botStatus.getStatus().equals(BotStatusEnums.ASK_ABOUT_TELEPHONE.toString())) {
                        botStatusService.updateBotStatus(chatId, BotStatusEnums.FINISH.toString());
                        adService.updateDescription(id, message);
                        askAboutTelephone(chatId);
                    } else if (botStatus.getStatus().equals(BotStatusEnums.FINISH.toString())) {
                        adService.updateTelephone(id, message);
                        adService.updateStatus(id, "waiting");
                        botStatusService.deleteBotStatus(chatId);
                        warnAboutCreationAd(chatId);
                    }
                }
            }
        }
        // Проверяем на наличие нажатой кнопки
        else if (update.hasCallbackQuery()) {
            // Считываем id чата
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            // Проверяем полученное значение кнопки
            String button = update.getCallbackQuery().getData();
            if (button.startsWith("1")) {
                // Создаем объект статуса бота
                botStatus = new BotStatus();
                ad = new Ad();
                // Устанавливаем значение chatId нашему объекту
                botStatus.setChatId(chatId);
                // Устанавливаем состояние бота
                botStatus.setStatus(BotStatusEnums.ASK_ABOUT_MODEL.toString());
                // Сохраняем в бд
                botStatus = botStatusService.save(botStatus);
                ad.setChatId(chatId);
                adService.saveAd(ad);
                // Отправляем вопрос
                askAboutBrand(chatId);
            } else if (button.startsWith("2") || button.startsWith("3")) {
                List<Ad> adList;
                if (button.startsWith("2")) {
                    adList = adService.readAll();
                } else {
                    adList = adService.findByChatId(chatId);
                }
                createMessage(chatId, adList.toString());
                showAds(chatId, adList);
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
    private void showAds(String chatId, List<Ad> adList) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        for (Ad ad : adList) {
            sendMessage.setText(
                    ad.getDescription() + "\n"
                    + "Бренд: " + ad.getBrand() + "\n"
                    + "Модель: " + ad.getModel() + "\n"
                    + "Год: " + ad.getYear() + "\n"
                    + "Пробег: " + ad.getMileage() + "\n"
                    + "Цена: " + ad.getPrice() + "\n"
                    + "Телефон для связи: " + ad.getTelephone() + "\n"
            );
            execute(sendMessage);
        }
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
