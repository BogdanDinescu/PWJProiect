package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.CityDto;
import com.bogdan.pharmacy.model.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {
    public CityDto cityToDto(City city) {
        return new CityDto(city.getId(), city.getName());
    }

    public City dtoToCity(CityDto city) {
        return new City(city.getId(), city.getName());
    }
}
