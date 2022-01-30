package by.overone.it.tg_bot;

import by.overone.it.dao.AdService;
import by.overone.it.dao.BotStatusService;
import by.overone.it.entity.Ad;
import by.overone.it.entity.BotStatus;
import by.overone.it.enums.AdStatusEnums;
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
    @Autowired
    private AdService adService;
    private Ad ad;
    private BotStatus botStatus;
    private final String BOT_NAME = "test_bot";
    private final String BOT_TOKEN = "5153744354:AAFufvHy_I6mTRVLQ8slD0ge8s_JA7oF6Og";
    private final String ADMIN_CHAT_ID = "743321260";

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
                    showMenu(chatId);
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
                        adService.updateStatus(id, AdStatusEnums.WAITING.toString());
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
            } else if (button.startsWith("2") || button.startsWith("3") || button.startsWith("4")) {
                List<Ad> adList;
                String role = "";
                if (button.startsWith("2")) {
                    adList = adService.getByStatus(AdStatusEnums.APPROVED.toString());
                    role = "USER";
                } else if (button.startsWith("3")) {
                    adList = adService.findByChatId(chatId);
                    role = "USER";
                } else {
                    adList = adService.getByStatus(AdStatusEnums.WAITING.toString());
                    role = "ADMIN";
                }
                createMessage(chatId, adList.toString());
                showAds(chatId, adList, role);
            } else if (button.startsWith("5")) {

            }
        }
    }

    private List<List<InlineKeyboardButton>> createUserButtons() {
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
        return new ArrayList<>(List.of(
                firstRow,
                secondRow
        ));
    }

    private InlineKeyboardMarkup createAdminMenuButtons() {
        InlineKeyboardButton adAdministration = new InlineKeyboardButton();
        adAdministration.setText("Управление объявлениями");
        adAdministration.setCallbackData("4");
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        thirdRow.add(adAdministration);
        List<List<InlineKeyboardButton>> inlineKeyboards = createUserButtons();
        inlineKeyboards.add(thirdRow);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(inlineKeyboards);
        return markup;
    }

    private InlineKeyboardMarkup createApproveAndRejectButtons() {
        InlineKeyboardButton approveButton = new InlineKeyboardButton();
        approveButton.setText("Принять");
        approveButton.setCallbackData("5");
        InlineKeyboardButton rejectButton = new InlineKeyboardButton();
        rejectButton.setText("Отклонить");
        rejectButton.setCallbackData("6");
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        buttonsRow.add(approveButton);
        buttonsRow.add(rejectButton);
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        inlineButtons.add(buttonsRow);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(inlineButtons);
        return markup;
    }

    @SneakyThrows
    // Метод, отвечающий за вывод приветственного меню. Содержит в себе текст и 3 кнопки
    private void showMenu(String chatId) {
        // Создаем текст приветственного сообщения
        SendMessage sendMessage = createMessage(chatId, Messages.getGreetingMessage());
        // Проверка на администратора
        if (chatId.equals(ADMIN_CHAT_ID)) {
            sendMessage.setReplyMarkup(createAdminMenuButtons());
        } else {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(createUserButtons());
            sendMessage.setReplyMarkup(markup);
        }
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
    private void showAds(String chatId, List<Ad> adList, String role) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (chatId.equals(ADMIN_CHAT_ID) && role.equals("ADMIN")) {
            sendMessage.setReplyMarkup(createApproveAndRejectButtons());
        }
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
    private void askAboutBrand(String chatId) {
        execute(createMessage(chatId, "Введите бренд автомобиля"));
    }

    @SneakyThrows
    private void askAboutModel(String chatId) {
        execute(createMessage(chatId, "Введите модель"));
    }

    @SneakyThrows
    private void askAboutMileage(String chatId) {
        execute(createMessage(chatId, "Введите пробег автомобиля"));
    }

    @SneakyThrows
    private void askAboutYear(String chatId) {
        execute(createMessage(chatId, "Введите год автомобиля"));
    }

    @SneakyThrows
    private void askAboutPrice(String chatId) {
        execute(createMessage(chatId, "Введите цену автомобиля"));
    }

    @SneakyThrows
    private void askAboutDescription(String chatId) {
        execute(createMessage(chatId, "Введите описание автомобиля"));
    }

    @SneakyThrows
    private void askAboutPhoto(String chatId) {
        execute(createMessage(chatId, "Загрузите фото автомобиля"));
    }

    @SneakyThrows
    private void askAboutTelephone(String chatId) {
        execute(createMessage(chatId, "Введите ваш номер"));
    }

    @SneakyThrows
    private void warnAboutCreationAd(String chatId) {
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
