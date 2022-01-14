package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.service.DrugService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrugController.class)
class DrugControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DrugService service;
    @MockBean
    private DrugMapper mapper;

    static DrugDto drugDto;
    static Drug drug;
    static DrugDto nonExistentDrugDto;
    static Drug nonExistingDrug;
    static DrugDto drugWithNonExitingProducerDto;
    static Drug drugWithNonExistingProducer;

    @BeforeEach
    void setUp() {
        drugDto = new DrugDto(1L, "Medicament", 1L);
        drug = new Drug(1L, "Medicament", new Producer(1L, "Producator"));
        nonExistentDrugDto = new DrugDto(2L, "Medicament2", 1L);
        nonExistingDrug = new Drug(2L, "Medicament2", new Producer(1L, "Producator"));
        drugWithNonExistingProducer = new Drug(3L, "Medicament3", new Producer(2L, "Producator fals"));
        drugWithNonExitingProducerDto = new DrugDto(3L, "Medicament3", 2L);
    }

    @Test
    void getDrugById() throws Exception {
        String endpoint = "/api/drug/%d";
        when(service.getDrug(drugDto.getId())).thenReturn(drug);
        when(service.getDrug(nonExistentDrugDto.getId())).thenThrow(new NotFoundException("drug"));
        mockMvc.perform(get(String.format(endpoint, drugDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistentDrugDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addDrug() throws Exception{
        String endpoint = "/api/drug";
        when(service.addDrug(drugDto)).thenReturn(drug);
        when(service.addDrug(drugWithNonExitingProducerDto)).thenThrow(new NotFoundException("producer"));
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drugDto))).andExpect(status().isCreated());
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drugWithNonExitingProducerDto))).andExpect(status().isNotFound());
    }

    @Test
    void updateDrug() throws Exception{
        String endpoint = "/api/drug/%d";
        when(service.updateDrug(drugDto.getId(), drugDto)).thenReturn(true);
        when(service.updateDrug(nonExistentDrugDto.getId(), nonExistentDrugDto)).thenReturn(false);
        when(service.updateDrug(drugWithNonExitingProducerDto.getId(), drugWithNonExitingProducerDto)).thenThrow(new NotFoundException("producer"));
        mockMvc.perform(put(String.format(endpoint, drugDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drugDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistentDrugDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentDrugDto))).andExpect(status().isCreated());
        mockMvc.perform(put(String.format(endpoint, drugWithNonExitingProducerDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(drugWithNonExitingProducerDto))).andExpect(status().isNotFound());
    }

    @Test
    void removeDrug() throws Exception {
        String endpoint = "/api/drug/%d";
        doThrow(new NotFoundException("pharmacy")).when(service).removeDrug(nonExistentDrugDto.getId());
        doNothing().when(service).removeDrug(drugDto.getId());
        mockMvc.perform(delete(String.format(endpoint, drugDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistentDrugDto.getId()))).andExpect(status().isNotFound());
    }
}