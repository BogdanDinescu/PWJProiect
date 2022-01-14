package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.PharmacyDto;
import com.bogdan.pharmacy.dao.PharmacyUI;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PharmacyMapper {
    private final CityService cityService;

    public PharmacyMapper(@Autowired CityService cityService) {
        this.cityService = cityService;
    }

    public PharmacyDto pharmacyToDto(Pharmacy pharmacy) {
        return new PharmacyDto(pharmacy.getId(), pharmacy.getAddress(), pharmacy.getCity().getId());
    }

    public PharmacyUI pharmacyToUI(Pharmacy pharmacy) {
        return new PharmacyUI(pharmacy.getId(), pharmacy.getAddress(), pharmacy.getCity().getName());
    }

    public Pharmacy dtoToPharmacy(PharmacyDto pharmacy) {
        City city = cityService.getCity(pharmacy.getCityId());
        return new Pharmacy(pharmacy.getId(), pharmacy.getAddress(), city);
    }

    public List<PharmacyUI> pharmacyListToUIList(List<Pharmacy> pharmacies) {
        return pharmacies.stream().map(this::pharmacyToUI).collect(Collectors.toList());
    }
}
