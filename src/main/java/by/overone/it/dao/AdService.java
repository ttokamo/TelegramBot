package by.overone.it.dao;

import by.overone.it.entity.Ad;
import by.overone.it.repository.AdRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    public void saveAd(Ad ad) {
        adRepository.save(ad);
    }

    public List<Ad> readAll() {
        return adRepository.findAll();
    }

    public void updateBrand(String id, String brand) {
        adRepository.updateAdBrand(id, brand);
    }

    public void updateModel(String id, String model) {
        adRepository.updateAdModel(id, model);
    }

    public void updateYear(String id, String year) {
        adRepository.updateAdYear(id, year);
    }

    public void updateMileage(String id, String mileage) {
        adRepository.updateAdMileage(id, mileage);
    }

    public void updatePrice(String id, String price) {
        adRepository.updateAdPrice(id, price);
    }

    public void updatePhoto(String id, String photo) {
        adRepository.updateAdPhoto(id, photo);
    }

    public void updateDescription(String id, String description) {
        adRepository.updateAdDescription(id, description);
    }

    public void updateTelephone(String id, String telephone) {
        adRepository.updateAdTelephone(id, telephone);
    }

    public void updateStatus(String id, String status) {
        adRepository.updateAdStatus(id, status);
    }

    public List<Ad> findByChatId(String chatId) {
        return adRepository.getByChatId(chatId);
    }
}
