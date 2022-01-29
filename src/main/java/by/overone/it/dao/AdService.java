package by.overone.it.dao;

import by.overone.it.entity.Ad;
import by.overone.it.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    public void saveAd(Ad ad) {
        adRepository.save(ad);
    }

    public void updateBrand(String chatId, String brand) {
        adRepository.updateAdBrand(chatId, brand);
    }

    public void updateModel(String chatId, String model) {
        adRepository.updateAdModel(chatId, model);
    }

    public void updateYear(String chatId, String year) {
        adRepository.updateAdYear(chatId, year);
    }

    public void updateMileage(String chatId, String mileage) {
        adRepository.updateAdMileage(chatId, mileage);
    }

    public void updatePrice(String chatId, String price) {
        adRepository.updateAdPrice(chatId, price);
    }

    public void updatePhoto(String chatId, String photo) {
        adRepository.updateAdPhoto(chatId, photo);
    }

    public void updateDescription(String chatId, String description) {
        adRepository.updateAdDescription(chatId, description);
    }

    public void updateTelephone(String chatId, String telephone) {
        adRepository.updateAdTelephone(chatId, telephone);
    }

    public void updateStatus(String chatId, String status) {
        adRepository.updateAdStatus(chatId, status);
    }
}
