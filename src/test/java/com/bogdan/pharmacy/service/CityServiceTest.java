package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.CityDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.CityMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CityServiceTest {

    @InjectMocks
    private CityService service;
    @Mock
    private CityRepository repository;
    @Mock
    private CityMapper mapper;

    private static CityDto existingCityDto;
    private static City existingCity;
    private static CityDto nonExistingCityDto;
    private static City nonExistingCity;

    @BeforeEach
    public void setup() {
        existingCityDto = new CityDto(1L, "Bucharest");
        existingCity = new City(1L, "Bucharest");
        nonExistingCity = new City(2L, "Atlantida");
        nonExistingCityDto = new CityDto(2L, "Atlantida");
    }

    @Test
    void getCity() {
        when(repository.findById(existingCityDto.getId())).thenReturn(Optional.ofNullable(existingCity));
        when(repository.findById(nonExistingCityDto.getId())).thenThrow(new NotFoundException("city"));
        assertThrows(NotFoundException.class, () -> {service.getCity(nonExistingCityDto.getId());});
        assertEquals(service.getCity(existingCityDto.getId()).getId(), existingCity.getId());
        assertEquals(service.getCity(existingCityDto.getId()).getName(), existingCity.getName());
    }

    @Test
    void addCity() {
        when(mapper.dtoToCity(existingCityDto)).thenReturn(existingCity);
        when(repository.save(existingCity)).thenReturn(existingCity);
        service.addCity(existingCityDto);
    }

    @Test
    void updateCity() {
        when(repository.existsById(existingCity.getId())).thenReturn(true);
        when(repository.existsById(nonExistingCity.getId())).thenReturn(false);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToCity(existingCityDto)).thenReturn(existingCity);
        when(mapper.dtoToCity(nonExistingCityDto)).thenReturn(nonExistingCity);
        assertTrue(service.updateCity(existingCityDto.getId(), existingCityDto));
        assertFalse(service.updateCity(nonExistingCityDto.getId(), nonExistingCityDto));
        assertFalse(service.updateCity(4L, existingCityDto));
    }

    @Test
    void removeCity() {
        when(repository.findById(existingCityDto.getId())).thenReturn(Optional.ofNullable(existingCity));
        when(repository.findById(nonExistingCityDto.getId())).thenThrow(new NotFoundException("city"));
        assertThrows(NotFoundException.class, () -> {service.removeCity(nonExistingCityDto.getId());});
        service.removeCity(existingCityDto.getId());
    }
}