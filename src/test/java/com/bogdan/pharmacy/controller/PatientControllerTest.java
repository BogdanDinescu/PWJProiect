package com.bogdan.pharmacy.controller;

import com.bogdan.pharmacy.dao.PatientDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.PatientMapper;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.model.Patient;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.service.PatientService;
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

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PatientService service;
    @MockBean
    private PatientMapper mapper;
    @MockBean
    private TreatmentMapper treatmentMapper;
    @MockBean
    private PharmacyMapper pharmacyMapper;

    static PatientDto patientDto;
    static Patient patient;
    static PatientDto nonExistentPatientDto;
    static Patient nonExistingPatient;
    static PatientDto patientWithNonExitingCityDto;
    static Patient patientWithNonCityTreatment;
    static Treatment existingTreatment;
    static Treatment nonExistingTreatment;

    @BeforeEach
    void setUp() {
        patientDto = new PatientDto(1L, "Popescu Ion", 1L);
        patient = new Patient(1L, "Popescu Ion", new City(1L, "Bucharest"));
        nonExistentPatientDto = new PatientDto(2L, "Popescu John", 1L);
        nonExistingPatient = new Patient(2L, "Popescu John", new City(1L, "Bucharest"));
        patientWithNonCityTreatment = new Patient(3L, "Popescu Ana", new City(2L, "Atlantida"));
        patientWithNonExitingCityDto = new PatientDto(3L, "Popescu Ana", 2L);
        existingTreatment = new Treatment(1L, "Tratament1");
        nonExistingTreatment = new Treatment(2L, "Tratament2");
    }

    @Test
    void getPatientById() throws Exception {
        String endpoint = "/api/patient/%d";
        when(service.getPatient(patient.getId())).thenReturn(patient);
        when(service.getPatient(nonExistentPatientDto.getId())).thenThrow(new NotFoundException("pharmacy"));
        mockMvc.perform(get(String.format(endpoint, patientDto.getId()))).andExpect(status().isOk());
        mockMvc.perform(get(String.format(endpoint, nonExistentPatientDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void getPatientTreatments() throws Exception {
        String endpoint = "/api/patient/%d/treatments";
        List<Treatment> treatmentList = new ArrayList<>();
        treatmentList.add(existingTreatment);
        patient.setTreatments(treatmentList);
        when(service.getPatient(patientDto.getId())).thenReturn(patient);
        when(service.getPatient(nonExistingPatient.getId())).thenThrow(new NotFoundException("patient"));
        mockMvc.perform(get(String.format(endpoint, patientDto.getId()))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(treatmentMapper.treatmentListToDtoList(treatmentList))));
        mockMvc.perform(get(String.format(endpoint, nonExistentPatientDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void addPatient() throws Exception{
        String endpoint = "/api/patient";
        when(service.addPatient(patientDto)).thenReturn(patient);
        when(service.addPatient(patientWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientDto))).andExpect(status().isCreated());
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientWithNonExitingCityDto))).andExpect(status().isNotFound());
    }

    @Test
    void updatePatient() throws Exception{
        String endpoint = "/api/patient/%d";
        when(service.updatePatient(patientDto.getId(), patientDto)).thenReturn(true);
        when(service.updatePatient(nonExistentPatientDto.getId(), nonExistentPatientDto)).thenReturn(false);
        when(service.updatePatient(patientWithNonExitingCityDto.getId(), patientWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        mockMvc.perform(put(String.format(endpoint, patientDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientDto))).andExpect(status().isNoContent());
        mockMvc.perform(put(String.format(endpoint, nonExistentPatientDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentPatientDto))).andExpect(status().isCreated());
        mockMvc.perform(put(String.format(endpoint, patientWithNonExitingCityDto.getId())).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientWithNonExitingCityDto))).andExpect(status().isNotFound());
    }

    @Test
    void addTreatment() throws Exception {
        String endpoint = "/api/patient/%d/treatments/%d";
        doThrow(new NotFoundException("patient")).when(service).addTreatment(nonExistentPatientDto.getId(), existingTreatment.getId());
        doThrow(new NotFoundException("treatment")).when(service).addTreatment(patientDto.getId(), nonExistingTreatment.getId());
        doNothing().when(service).addTreatment(patientDto.getId(), existingTreatment.getId());
        mockMvc.perform(post(String.format(endpoint, patientDto.getId(), existingTreatment.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(post(String.format(endpoint, nonExistingPatient.getId(), existingTreatment.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(post(String.format(endpoint, patientDto.getId(), nonExistingTreatment.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void removePatient() throws Exception {
        String endpoint = "/api/patient/%d";
        doThrow(new NotFoundException("patient")).when(service).removePatient(nonExistentPatientDto.getId());
        doNothing().when(service).removePatient(patientDto.getId());
        mockMvc.perform(delete(String.format(endpoint, patientDto.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistentPatientDto.getId()))).andExpect(status().isNotFound());
    }

    @Test
    void removeTreatment() throws Exception {
        String endpoint = "/api/patient/%d/treatments/%d";
        doThrow(new NotFoundException("patient")).when(service).removeTreatment(nonExistentPatientDto.getId(), existingTreatment.getId());
        doThrow(new NotFoundException("treatment")).when(service).removeTreatment(patientDto.getId(), nonExistingTreatment.getId());
        doNothing().when(service).removeTreatment(patientDto.getId(), existingTreatment.getId());
        mockMvc.perform(delete(String.format(endpoint, patientDto.getId(), existingTreatment.getId()))).andExpect(status().isNoContent());
        mockMvc.perform(delete(String.format(endpoint, nonExistingPatient.getId(), existingTreatment.getId()))).andExpect(status().isNotFound());
        mockMvc.perform(delete(String.format(endpoint, patientDto.getId(), nonExistingTreatment.getId()))).andExpect(status().isNotFound());
    }
}