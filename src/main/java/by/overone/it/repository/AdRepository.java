package by.overone.it.repository;

import by.overone.it.entity.Ad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
@Transactional
public interface AdRepository extends CrudRepository<Ad, String> {

    @Modifying
    @Query("update Ad set brand =:brand where chatId =:chatId")
    void updateAdBrand(@Param("chatId") String chatId, @Param("brand") String brand);

    @Modifying
    @Query("update Ad set model =:model where chatId =:chatId")
    void updateAdModel(@Param("chatId") String chatId, @Param("model") String model);

    @Modifying
    @Query("update Ad set year =:year where chatId =:chatId")
    void updateAdYear(@Param("chatId") String chatId, @Param("year") String year);

    @Modifying
    @Query("update Ad set mileage =:mileage where chatId =:chatId")
    void updateAdMileage(@Param("chatId") String chatId, @Param("mileage") String mileage);

    @Modifying
    @Query("update Ad set price =:price where chatId =:chatId")
    void updateAdPrice(@Param("chatId") String chatId, @Param("price") BigDecimal price);

    @Modifying
    @Query("update Ad set photo =:photo where chatId =:chatId")
    void updateAdPhoto(@Param("chatId") String chatId, @Param("photo") String photo);

    @Modifying
    @Query("update Ad set description =:description where chatId =:chatId")
    void updateAdDescription(@Param("chatId") String chatId, @Param("description") String description);

    @Modifying
    @Query("update Ad set telephone =:telephone where chatId =:chatId")
    void updateAdTelephone(@Param("chatId") String chatId, @Param("telephone") String telephone);

    @Modifying
    @Query("update Ad set status =:status where chatId =:chatId")
    void updateAdStatus(@Param("chatId") String chatId, @Param("status") String status);
}
