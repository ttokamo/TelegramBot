package by.overone.it.repository;

import by.overone.it.entity.BotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface BotStatusRepository extends CrudRepository<BotStatus, Long> {
}
