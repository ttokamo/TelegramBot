package by.overone.it.dao;

import by.overone.it.entity.Ad;
import by.overone.it.repository.AdRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
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

    public String getStatusById(String id) {
        return adRepository.getStatusById(id);
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

    @SneakyThrows
    public void updatePhoto(String id, File photo) {
        byte[] bPhoto = new byte[(int) photo.length()];

        FileInputStream fileInputStream = new FileInputStream(photo);
        fileInputStream.read(bPhoto);
        fileInputStream.close();

        adRepository.updateAdPhoto(id, bPhoto);
    }

    public void updateDescription(String id, String description) {
        adRepository.updateAdDescription(id, description);
    }

    public void updateTelephone(String id, String telephone) {
        adRepository.updateAdTelephone(id, telephone);
    }

    public String updateStatus(String id, String status) {
        adRepository.updateAdStatus(id, status);
        return "";
    }

    public void deleteAd(String id) {
        adRepository.deleteById(id);
    }

    public List<Ad> getByStatus(String status) {
        return adRepository.getByStatus(status);
    }

    public List<Ad> findByChatId(String chatId) {
        return adRepository.getByChatId(chatId);
    }
}
