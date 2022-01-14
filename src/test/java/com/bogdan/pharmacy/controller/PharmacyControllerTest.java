package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.PharmacyDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.service.PharmacyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PharmacyController.class)
class PharmacyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PharmacyService service;
    @MockBean
    private PharmacyMapper mapper;
    @MockBean
    private DrugMapper drugMapper;

    static PharmacyDto pharmacyDto;
    static Pharmacy pharmacy;
    static PharmacyDto nonExistentPharmacyDto;
    static Pharmacy nonExistingPharmacy;
    static PharmacyDto pharmacyWithNonExitingCityDto;
    static Pharmacy pharmacyWithNonExistingCity;
    static Drug existingDrug;
    static Drug nonExistingDrug;

    @BeforeEach
    void setUp() {
        pharmacyDto = new PharmacyDto(1L, "Str Florilor", 1L);
        pharmacy = new Pharmacy(1L, "Str Florilor", new City(1L, "Bucharest"));
        nonExistentPharmacyDto = new PharmacyDto(2L, "Str Lalelelor", 1L);
        nonExistingPharmacy = new Pharmacy(2L, "Str Lalelelor", new City(1L, "Bucharest"));
        pharmacyWithNonExistingCity = new Pharmacy(3L, "Str Trandafirilor", new City(2L, "Atlantida"));
        pharmacyWithNonExitingCityDto = new PharmacyDto(3L, "Str Trandafirilor", 2L);
        existingDrug = new Drug(1L, "Medicament", new Producer(1L, "Terapia"));
        nonExistingDrug = new Drug(2L, "Panaceu", new Producer(1L, "Panaceu"));
    }

    @Test
    void getPharmacyById() throws Exception {
        String endpoint = "/api/pharmacy/%d";
        when(service.getPharmacy(pharmacy.getId())).thenReturn(pharmacy);
        when(service.getPharmacy(nonExistingPharmacy.getId())).thenThrow(new NotFoundException("pharmacy"));
        mockMvc.perform(get(String.format(endpoint, pharmacyDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistentPharmacyDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void getAllPharmacies() throws Exception {
        String endpoint = "/api/pharmacy";
        when(service.getPharmacies()).thenReturn(List.of(pharmacy));
        mockMvc.perform(get(endpoint)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.pharmacyListToUIList(List.of(pharmacy)))));
    }

    @Test
    void getPharmacyDrugs() throws Exception {
        String endpoint = "/api/pharmacy/%d/drugs";
        List<Drug> drugList = new ArrayList<>();
        drugList.add(existingDrug);
        pharmacy.setDrugs(drugList);
        when(service.getPharmacy(pharmacy.getId())).thenReturn(pharmacy);
        when(service.getPharmacy(nonExistingPharmacy.getId())).thenThrow(new NotFoundException("pharmacy"));
        mockMvc.perform(get(String.format(endpoint, pharmacyDto.getId()))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(drugMapper.drugListToDtoList(drugList))));
        mockMvc.perform(get(String.format(endpoint, nonExistentPharmacyDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addPharmacy() throws Exception {
        String endpoint = "/api/pharmacy";
        when(service.addPharmacy(pharmacyDto)).thenReturn(pharmacy);
        when(service.addPharmacy(pharmacyWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pharmacyDto))).andExpect(status().isCreated());
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pharmacyWithNonExitingCityDto))).andExpect(status().isNotFound());
    }

    @Test
    void addDrug() throws Exception {
        String endpoint = "/api/pharmacy/%d/drugs/%d";
        doThrow(new NotFoundException("pharmacy")).when(service).addDrug(nonExistentPharmacyDto.getId(), existingDrug.getId());
        doThrow(new NotFoundException("drug")).when(service).addDrug(pharmacyDto.getId(), nonExistingDrug.getId());
        doNothing().when(service).addDrug(pharmacyDto.getId(), existingDrug.getId());
        mockMvc.perform(post(String.format(endpoint, pharmacyDto.getId(), existingDrug.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(post(String.format(endpoint, nonExistingPharmacy.getId(), existingDrug.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(post(String.format(endpoint, pharmacyDto.getId(), nonExistingDrug.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void updatePharmacy() throws Exception{
        String endpoint = "/api/pharmacy/%d";
        when(service.updatePharmacy(pharmacyDto.getId(), pharmacyDto)).thenReturn(true);
        when(service.updatePharmacy(nonExistentPharmacyDto.getId(), nonExistentPharmacyDto)).thenReturn(false);
        when(service.updatePharmacy(pharmacyWithNonExitingCityDto.getId(), pharmacyWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        mockMvc.perform(put(String.format(endpoint, pharmacyDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pharmacyDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistentPharmacyDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentPharmacyDto))).andExpect(status().isCreated());
        mockMvc.perform(put(String.format(endpoint, pharmacyWithNonExitingCityDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pharmacyWithNonExitingCityDto))).andExpect(status().isNotFound());
    }

    @Test
    void removePharmacy() throws Exception {
        String endpoint = "/api/pharmacy/%d";
        doThrow(new NotFoundException("pharmacy")).when(service).removePharmacy(nonExistentPharmacyDto.getId());
        doNothing().when(service).removePharmacy(pharmacyDto.getId());
        mockMvc.perform(delete(String.format(endpoint, pharmacyDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistentPharmacyDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void removeDrug() throws Exception {
        String endpoint = "/api/pharmacy/%d/drugs/%d";
        doThrow(new NotFoundException("pharmacy")).when(service).removeDrug(nonExistentPharmacyDto.getId(), existingDrug.getId());
        doThrow(new NotFoundException("drug")).when(service).removeDrug(pharmacyDto.getId(), nonExistingDrug.getId());
        doNothing().when(service).removeDrug(pharmacyDto.getId(), existingDrug.getId());
        mockMvc.perform(delete(String.format(endpoint, pharmacyDto.getId(), existingDrug.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistingPharmacy.getId(), existingDrug.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(delete(String.format(endpoint, pharmacyDto.getId(), nonExistingDrug.getId()))).andExpect(status().isNotFound());
    }
}