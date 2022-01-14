package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.CityDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.CityMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.service.CityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CityService service;
    @MockBean
    private CityMapper mapper;

    private static CityDto existingCityDto;
    private static City existingCity;
    private static CityDto nonExistingCityDto;
    private static City nonExistingCity;

    @BeforeEach
    void setUp() {
        existingCityDto = new CityDto(1L, "Bucharest");
        existingCity = new City(1L, "Bucharest");
        nonExistingCity = new City(2L, "Atlantida");
        nonExistingCityDto = new CityDto(2L, "Atlantida");
    }

    @Test
    void getCityById() throws Exception {
        String endpoint = "/api/city/%d";
        when(service.getCity(existingCity.getId())).thenReturn(existingCity);
        when(service.getCity(nonExistingCity.getId())).thenThrow(new NotFoundException("producer"));
        mockMvc.perform(get(String.format(endpoint, existingCityDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistingCityDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addCity() throws Exception{
        String endpoint = "/api/city";
        when(service.addCity(existingCityDto)).thenReturn(existingCity);
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingCityDto))).andExpect(status().isCreated());
    }

    @Test
    void updateCity() throws Exception{
        String endpoint = "/api/city/%d";
        when(service.updateCity(existingCityDto.getId(), existingCityDto)).thenReturn(true);
        when(service.updateCity(nonExistingCityDto.getId(), nonExistingCityDto)).thenReturn(false);
        mockMvc.perform(put(String.format(endpoint, existingCityDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingCityDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistingCityDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistingCityDto))).andExpect(status().isCreated());
    }

    @Test
    void removeCity() throws Exception {
        String endpoint = "/api/city/%d";
        doThrow(new NotFoundException("city")).when(service).removeCity(nonExistingCityDto.getId());
        doNothing().when(service).removeCity(existingCityDto.getId());
        mockMvc.perform(delete(String.format(endpoint, existingCityDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistingCityDto.getId()))).andExpect(status().isNotFound());
    }
}