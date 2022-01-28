package by.overone.it.repository;

import by.overone.it.entity.BotStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


// Интерфейс, с помощью которого выполняются запросы в базу данных
@Repository
public interface BotStatusRepository extends CrudRepository<BotStatus, Long> {
}
