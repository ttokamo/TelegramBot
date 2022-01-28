package by.overone.it.dao;

import by.overone.it.entity.BotStatus;
import by.overone.it.repository.BotStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Component
public class BotStatusDao implements BotStatusService {

    @Autowired
    private BotStatusRepository repository;

    @Override
    public BotStatus save(BotStatus botStatus) {
        return repository.save(botStatus);
    }
}
