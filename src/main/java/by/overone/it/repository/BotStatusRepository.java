package by.overone.it.repository;

import by.overone.it.entity.BotStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


// Интерфейс, с помощью которого выполняются запросы в базу данных
@Repository
@Transactional
public interface BotStatusRepository extends CrudRepository<BotStatus, String> {

    @Modifying
    @Query("update BotStatus set status =:status where chatId =:chatId")
    void updateBotStatus(@Param("chatId") String chatId, @Param("status") String status);

    @Modifying
    @Query("delete BotStatus where chatId =:chatId")
    void deleteBotStatus(@Param("chatId") String chatId);

    BotStatus findFirstByChatId(String chatId);
}
