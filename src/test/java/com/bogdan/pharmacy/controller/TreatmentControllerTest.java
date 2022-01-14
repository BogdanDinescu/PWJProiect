package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.service.TreatmentService;
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

@WebMvcTest(TreatmentController.class)
class TreatmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TreatmentService service;
    @MockBean
    private TreatmentMapper mapper;
    @MockBean
    private DrugMapper drugMapper;

    static TreatmentDto treatmentDto;
    static Treatment treatment;
    static TreatmentDto nonExistentTreatmentDto;
    static Treatment nonExistingTreatment;
    static Drug existingDrug;
    static Drug nonExistingDrug;

    @BeforeEach
    void setUp() {
        treatmentDto = new TreatmentDto(1L, "Tratament");
        treatment = new Treatment(1L, "Tratament");
        nonExistentTreatmentDto = new TreatmentDto(2L, "Tratament fals");
        nonExistingTreatment = new Treatment(2L, "Tratament fals");
        existingDrug = new Drug(1L, "Medicament", new Producer(1L, "Terapia"));
        nonExistingDrug = new Drug(2L, "Panaceu", new Producer(1L, "Panaceu"));
    }

    @Test
    void getTreatmentById() throws Exception {
        String endpoint = "/api/treatment/%d";
        when(service.getTreatment(treatment.getId())).thenReturn(treatment);
        when(service.getTreatment(nonExistingTreatment.getId())).thenThrow(new NotFoundException("pharmacy"));
        mockMvc.perform(get(String.format(endpoint, treatmentDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistentTreatmentDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void getTreatmentDrugs() throws Exception{
        String endpoint = "/api/treatment/%d/drugs";
        List<Drug> drugList = new ArrayList<>();
        drugList.add(existingDrug);
        treatment.setDrugs(drugList);
        when(service.getTreatment(treatment.getId())).thenReturn(treatment);
        when(service.getTreatment(nonExistingTreatment.getId())).thenThrow(new NotFoundException("treatment"));
        mockMvc.perform(get(String.format(endpoint, treatmentDto.getId()))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(drugMapper.drugListToDtoList(drugList))));
        mockMvc.perform(get(String.format(endpoint, nonExistentTreatmentDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addTreatment() throws Exception {
        String endpoint = "/api/treatment";
        when(service.addTreatment(treatmentDto)).thenReturn(treatment);
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(treatmentDto))).andExpect(status().isCreated());
    }

    @Test
    void updateTreatment() throws Exception{
        String endpoint = "/api/treatment/%d";
        when(service.updateTreatment(treatmentDto.getId(), treatmentDto)).thenReturn(true);
        when(service.updateTreatment(nonExistentTreatmentDto.getId(), nonExistentTreatmentDto)).thenReturn(false);
        mockMvc.perform(put(String.format(endpoint, treatmentDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(treatmentDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistentTreatmentDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentTreatmentDto))).andExpect(status().isCreated());
    }

    @Test
    void addDrug() throws Exception {
        String endpoint = "/api/treatment/%d/drugs/%d";
        doThrow(new NotFoundException("treatment")).when(service).addDrug(nonExistentTreatmentDto.getId(), existingDrug.getId());
        doThrow(new NotFoundException("drug")).when(service).addDrug(treatmentDto.getId(), nonExistingDrug.getId());
        doNothing().when(service).addDrug(treatmentDto.getId(), existingDrug.getId());
        mockMvc.perform(post(String.format(endpoint, treatmentDto.getId(), existingDrug.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(post(String.format(endpoint, nonExistingTreatment.getId(), existingDrug.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(post(String.format(endpoint, treatmentDto.getId(), nonExistingDrug.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void removeTreatment() throws Exception {
        String endpoint = "/api/treatment/%d";
        doThrow(new NotFoundException("treatment")).when(service).removeTreatment(nonExistentTreatmentDto.getId());
        doNothing().when(service).removeTreatment(treatmentDto.getId());
        mockMvc.perform(delete(String.format(endpoint, treatmentDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistentTreatmentDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void removeDrug() throws Exception {
        String endpoint = "/api/treatment/%d/drugs/%d";
        doThrow(new NotFoundException("treatment")).when(service).removeDrug(nonExistingTreatment.getId(), existingDrug.getId());
        doThrow(new NotFoundException("drug")).when(service).removeDrug(treatmentDto.getId(), nonExistingDrug.getId());
        doNothing().when(service).removeDrug(treatmentDto.getId(), existingDrug.getId());
        mockMvc.perform(delete(String.format(endpoint, treatmentDto.getId(), existingDrug.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistentTreatmentDto.getId(), existingDrug.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(delete(String.format(endpoint, treatmentDto.getId(), nonExistingDrug.getId()))).andExpect(status().isNotFound());
    }
}