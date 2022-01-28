package by.overon.it.repository;

import by.overon.it.entity.Ad;

import java.util.List;

public class CarsAdsDao {
    private CarsAdsRepository carsAdsRepository;

    public void saveAd(Ad ad) {
        carsAdsRepository.save(ad);
    }
}
