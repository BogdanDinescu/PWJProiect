package com.bogdan.pharmacy.service;


import com.bogdan.pharmacy.dao.PatientDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.PatientMapper;
import com.bogdan.pharmacy.model.*;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @InjectMocks
    private PatientService service;
    @Mock
    private PatientRepository repository;
    @Mock
    private PatientMapper mapper;
    @Mock
    private TreatmentService treatmentService;

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
    void getPatient() {
        when(repository.findById(patientDto.getId())).thenReturn(Optional.ofNullable(patient));
        when(repository.findById(nonExistentPatientDto.getId())).thenThrow(new NotFoundException("patient"));
        assertThrows(NotFoundException.class, () -> {service.getPatient(nonExistentPatientDto.getId());});
        assertEquals(service.getPatient(patientDto.getId()).getId(), patient.getId());
        assertEquals(service.getPatient(patientDto.getId()).getName(), patient.getName());
        assertEquals(service.getPatient(patientDto.getId()).getCity().getId(), patient.getCity().getId());
    }

    @Test
    void addPatient() {
        when(mapper.dtoToPatient(patientWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        when(mapper.dtoToPatient(patientDto)).thenReturn(patient);
        when(repository.save(patient)).thenReturn(patient);
        assertThrows(NotFoundException.class, () -> {service.addPatient(patientWithNonExitingCityDto);});
        service.addPatient(patientDto);
    }

    @Test
    void updatePatient() {
        when(repository.existsById(patient.getId())).thenReturn(true);
        when(repository.existsById(nonExistingPatient.getId())).thenReturn(false);
        when(repository.existsById(patientWithNonCityTreatment.getId())).thenReturn(true);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToPatient(patientDto)).thenReturn(patient);
        when(mapper.dtoToPatient(nonExistentPatientDto)).thenReturn(nonExistingPatient);
        when(mapper.dtoToPatient(patientWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        assertThrows(NotFoundException.class, () -> {service.updatePatient(3L, patientWithNonExitingCityDto); });
        assertTrue(service.updatePatient(patientDto.getId(), patientDto));
        assertFalse(service.updatePatient(nonExistentPatientDto.getId(), nonExistentPatientDto));
        assertFalse(service.updatePatient(4L, patientDto));
    }

    @Test
    void removePatient() {
        when(repository.findById(patientDto.getId())).thenReturn(Optional.ofNullable(patient));
        when(repository.findById(nonExistentPatientDto.getId())).thenThrow(new NotFoundException("patient"));
        assertThrows(NotFoundException.class, () -> {service.removePatient(nonExistentPatientDto.getId());});
        service.removePatient(patientDto.getId());
    }


    @Test
    void addTreatment() {
        when(repository.findById(patientDto.getId())).thenReturn(Optional.ofNullable(patient));
        when(treatmentService.getTreatment(existingTreatment.getId())).thenReturn(existingTreatment);
        when(treatmentService.getTreatment(nonExistingTreatment.getId())).thenThrow(new NotFoundException("treatment"));
        patient.setTreatments(new ArrayList<>());
        service.addTreatment(patient.getId(), existingTreatment.getId());
        assertThrows(NotFoundException.class, () -> {service.addTreatment(patient.getId(), nonExistingTreatment.getId());});
    }

    @Test
    void removeTreatment() {
        when(repository.findById(patient.getId())).thenReturn(Optional.ofNullable(patient));
        when(treatmentService.getTreatment(existingTreatment.getId())).thenReturn(existingTreatment);
        when(treatmentService.getTreatment(nonExistingTreatment.getId())).thenThrow(new NotFoundException("treatment"));
        patient.setTreatments(new ArrayList<>());
        patient.addTreatment(existingTreatment);
        service.removeTreatment(patient.getId(), existingTreatment.getId());
        assertThrows(NotFoundException.class, () -> {service.removeTreatment(patient.getId(), nonExistingTreatment.getId());});
    }
}
