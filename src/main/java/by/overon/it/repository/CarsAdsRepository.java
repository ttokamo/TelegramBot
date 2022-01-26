package by.overon.it.repository;

import by.overon.it.entity.Ad;
import org.springframework.data.repository.CrudRepository;

public interface CarsAdsRepository extends CrudRepository<Ad, Long> {
}
