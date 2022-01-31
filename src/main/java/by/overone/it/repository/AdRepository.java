package by.overone.it.repository;

import by.overone.it.entity.Ad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AdRepository extends CrudRepository<Ad, String> {

    @Override
    List<Ad> findAll();

    @Modifying
    @Query("update Ad set brand =:brand where id =:id")
    void updateAdBrand(@Param("id") String id, @Param("brand") String brand);

    @Modifying
    @Query("update Ad set model =:model where id =:id")
    void updateAdModel(@Param("id") String id, @Param("model") String model);

    @Modifying
    @Query("update Ad set year =:year where id =:id")
    void updateAdYear(@Param("id") String id, @Param("year") String year);

    @Modifying
    @Query("update Ad set mileage =:mileage where id =:id")
    void updateAdMileage(@Param("id") String id, @Param("mileage") String mileage);

    @Modifying
    @Query("update Ad set price =:price where id =:id")
    void updateAdPrice(@Param("id") String id, @Param("price") String price);

    @Modifying
    @Query("update Ad set photo =:photo where id =:id")
    void updateAdPhoto(@Param("id") String id, @Param("photo") byte[] photo);

    @Modifying
    @Query("update Ad set description =:description where id =:id")
    void updateAdDescription(@Param("id") String id, @Param("description") String description);

    @Modifying
    @Query("update Ad set telephone =:telephone where id =:id")
    void updateAdTelephone(@Param("id") String id, @Param("telephone") String telephone);

    @Modifying
    @Query("update Ad set status =:status where id =:id")
    void updateAdStatus(@Param("id") String id, @Param("status") String status);

    @Query("select status from Ad where id =:id")
    String getStatusById(@Param("id") String id);

    @Query("from Ad where status =:status")
    List<Ad> getByStatus(@Param("status") String status);

    @Query("from Ad where chatId =:chatId")
    List<Ad> getByChatId(@Param("chatId") String chatId);
}
