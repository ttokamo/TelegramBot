package by.overone.it.dao;

import by.overone.it.entity.BotStatus;
import by.overone.it.repository.BotStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BotStatusService {

    @Autowired
    private BotStatusRepository repository;

    public BotStatus save(BotStatus botStatus) {
        return repository.save(botStatus);
    }

    public Optional<BotStatus> findById(String chatId) {
        return repository.findById(chatId);
    }

    public BotStatus findFirstByChatId(String chatId) {
        return repository.findFirstByChatId(chatId);
    }

    public void updateBotStatus(String chatId, String status) {
        repository.updateBotStatus(chatId, status);
    }
}
