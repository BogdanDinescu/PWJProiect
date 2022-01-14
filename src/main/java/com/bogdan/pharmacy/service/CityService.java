package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.CityDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.CityMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService {
    private final CityRepository repository;
    private final CityMapper mapper;

    public CityService(@Autowired CityRepository repository, @Autowired CityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public City getCity(Long id) {
        Optional<City> city = repository.findById(id);
        if (city.isEmpty()) {
            throw new NotFoundException("city");
        }
        return city.get();
    }

    public City addCity(CityDto city) {
        city.setId(null);
        return repository.save(mapper.dtoToCity(city));
    }

    public boolean updateCity(Long id, CityDto cityDto) {
        boolean exists = repository.existsById(id);
        City city = mapper.dtoToCity(cityDto);
        city.setId(id);
        repository.save(city);
        return exists;
    }

    public void removeCity(Long id) {
        City city = getCity(id);
        repository.delete(city);
    }
}
