package by.overone.it.dao;

import by.overone.it.entity.BotStatus;
import by.overone.it.repository.BotStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotStatusDao {

    @Autowired
    private BotStatusRepository repository;

    public BotStatus save(BotStatus botStatus) {
        return repository.save(botStatus);
    }
}
