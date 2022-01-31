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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
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
    private final String BOT_TOKEN = "5153744354:AAEokAPKCxKU0nfsA0HtFXYdHAQEgRkNNzs";
    private final String ADMIN_CHAT_ID = "743321260";

    @SneakyThrows
    @Override
    // Метод, который вызывается при запросе пользователя
    public synchronized void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            if (message != null) {
                String chatId = update.getMessage().getChatId().toString();
                if (message.startsWith("/start")) {
                    showMenu(chatId);
                } else if (botStatus != null && update.getMessage().hasText()) {
                    String id = ad.getId();
                    botStatus = botStatusService.findFirstByChatId(chatId);
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
        else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String button = update.getCallbackQuery().getData();

            if (button.startsWith("1")) {
                botStatus = botStatusService.findById(chatId);
                if (botStatus != null) {
                    botStatusService.deleteBotStatus(chatId);
                    adService.deleteAd(ad.getId());
                }
                botStatus = new BotStatus();
                ad = new Ad();
                botStatus.setChatId(chatId);
                botStatus.setStatus(BotStatusEnums.ASK_ABOUT_MODEL.toString());
                botStatus = botStatusService.save(botStatus);
                ad.setChatId(chatId);
                adService.saveAd(ad);
                askAboutBrand(chatId);
            } else if (button.startsWith("2") || button.startsWith("3") || button.startsWith("4")) {
                List<Ad> adList;
                String role;

                if (button.startsWith("2")) {
                    adList = adService.getByStatus(AdStatusEnums.APPROVED.toString());
                    role = "ALL_ADS";
                } else if (button.startsWith("3")) {
                    adList = adService.findByChatId(chatId);
                    role = "MY_ADS";
                } else {
                    adList = adService.getByStatus(AdStatusEnums.WAITING.toString());
                    role = "ADMIN";
                }

                createMessage(chatId, adList.toString());
                showAds(chatId, adList, role);

            } else if (button.startsWith("hidden")) {

                String[] buttonText = splitCallback(button);
                String id = buttonText[1];
                adService.updateStatus(id, AdStatusEnums.HIDDEN.toString());
                execute(createEditMessage(
                        chatId,
                        update.getCallbackQuery().getMessage().getMessageId(),
                        createShowMyAdButton("show " + id, id)));

            } else if (button.startsWith("show")) {

                String[] buttonText = splitCallback(button);
                String id = buttonText[1];
                adService.updateStatus(id, AdStatusEnums.APPROVED.toString());
                execute(createEditMessage(
                        chatId,
                        update.getCallbackQuery().getMessage().getMessageId(),
                        createHiddenMyAdButton("hidden " + id, id)));

            } else if (button.startsWith("approve")) {
                String[] buttonText = splitCallback(button);
                adService.updateStatus(buttonText[1], AdStatusEnums.APPROVED.toString());
                execute(deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));

            } else if (button.startsWith("reject") || button.startsWith("remove")) {
                String[] buttonText = splitCallback(button);
                adService.deleteAd(buttonText[1]);
                execute(deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
            }
        }
    }

    private List<List<InlineKeyboardButton>> createUserButtons() {
        InlineKeyboardButton createAd = new InlineKeyboardButton();
        createAd.setText("Создать объявление");
        createAd.setCallbackData("1");
        InlineKeyboardButton showAd = new InlineKeyboardButton();
        showAd.setText("Показать объявления");
        showAd.setCallbackData("2");
        InlineKeyboardButton myAd = new InlineKeyboardButton();
        myAd.setText("Мои объявления");
        myAd.setCallbackData("3");
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        firstRow.add(createAd);
        firstRow.add(showAd);
        secondRow.add(myAd);
        return new ArrayList<>(List.of(
                firstRow,
                secondRow
        ));
    }

    private EditMessageReplyMarkup createEditMessage(String chatId, Integer messageId, InlineKeyboardMarkup markup) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(markup);
        return editMessageReplyMarkup;
    }

    private InlineKeyboardMarkup createShowMyAdButton(String callback, String id) {
        InlineKeyboardButton hiddenAd = new InlineKeyboardButton();
        hiddenAd.setText("Показывать");
        hiddenAd.setCallbackData(callback);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(hiddenAd);
        row.add(createDeleteMyAdButton(id));
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        buttonList.add(row);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buttonList);
        return markup;
    }

    private InlineKeyboardMarkup createHiddenMyAdButton(String callback, String id) {
        InlineKeyboardButton hiddenAd = new InlineKeyboardButton();
        hiddenAd.setText("Остановить показ");
        hiddenAd.setCallbackData(callback);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(hiddenAd);
        row.add(createDeleteMyAdButton(id));
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        buttonList.add(row);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buttonList);
        return markup;
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

    private InlineKeyboardButton createDeleteMyAdButton(String id) {
        InlineKeyboardButton deleteMyAdButton = new InlineKeyboardButton();
        deleteMyAdButton.setText("Удалить");
        deleteMyAdButton.setCallbackData("remove " + id);
        return deleteMyAdButton;
    }

    private InlineKeyboardMarkup createSingleDeleteMyAdButton(String id) {
        List<InlineKeyboardButton> deleteMyAddButton = new ArrayList<>();
        deleteMyAddButton.add(createDeleteMyAdButton(id));
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(deleteMyAddButton);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buttons);
        return markup;
    }

    private InlineKeyboardMarkup createApproveAndRejectButtons(String approveCallback, String rejectCallback) {
        InlineKeyboardButton approveButton = new InlineKeyboardButton();
        approveButton.setText("Принять");
        approveButton.setCallbackData(approveCallback);

        InlineKeyboardButton rejectButton = new InlineKeyboardButton();
        rejectButton.setText("Отклонить");
        rejectButton.setCallbackData(rejectCallback);

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
    private void showMenu(String chatId) {
        SendMessage sendMessage = createMessage(chatId, Messages.getGreetingMessage());
        if (chatId.equals(ADMIN_CHAT_ID)) {
            sendMessage.setReplyMarkup(createAdminMenuButtons());
        } else {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(createUserButtons());
            sendMessage.setReplyMarkup(markup);
        }
        execute(sendMessage);
    }

    private SendMessage createMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    private DeleteMessage deleteMessage(String chatId, Integer messageId) {
        return new DeleteMessage(chatId, messageId);
    }

    @SneakyThrows
    private void showAds(String chatId, List<Ad> adList, String role) {
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
            if (chatId.equals(ADMIN_CHAT_ID) && role.equals("ADMIN")) {
                sendMessage.setReplyMarkup(createApproveAndRejectButtons(
                        "approve " + ad.getId(), "reject " + ad.getId()));
            } else if (role.equals("MY_ADS")) {
                if (adService.getStatusById(ad.getId()).equals(AdStatusEnums.APPROVED.toString())) {
                    sendMessage.setReplyMarkup(createHiddenMyAdButton("hidden " + ad.getId(), ad.getId()));
                } else if (adService.getStatusById(ad.getId()).equals(AdStatusEnums.HIDDEN.toString())) {
                    sendMessage.setReplyMarkup(createShowMyAdButton("show " + ad.getId(), ad.getId()));
                } else {
                    sendMessage.setReplyMarkup(createSingleDeleteMyAdButton(chatId));
                }
            }
            execute(sendMessage);
        }
    }

    private String[] splitCallback(String button) {
        return button.split("\\s");
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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(createUserButtons());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Объявление успешно создано и отправлено на рассмотрение администратору");
        execute(sendMessage);
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
