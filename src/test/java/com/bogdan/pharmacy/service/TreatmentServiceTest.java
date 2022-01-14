package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.repository.TreatmentRepository;
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
public class TreatmentServiceTest {
    @InjectMocks
    private TreatmentService service;
    @Mock
    private TreatmentRepository repository;
    @Mock
    private TreatmentMapper mapper;
    @Mock
    private DrugService drugService;

    static TreatmentDto treatmentDto;
    static Treatment treatment;
    static TreatmentDto nonExistentTreatmentDto;
    static Treatment nonExistingTreatment;
    static Drug existingDrug;
    static Drug nonExistingDrug;

    @BeforeEach
    public void setup() {
        treatmentDto = new TreatmentDto(1L, "Tratament");
        treatment = new Treatment(1L, "Tratament");
        nonExistentTreatmentDto = new TreatmentDto(2L, "Tratament fals");
        nonExistingTreatment = new Treatment(2L, "Tratament fals");
        existingDrug = new Drug(1L, "Medicament", new Producer(1L, "Terapia"));
        nonExistingDrug = new Drug(2L, "Panaceu", new Producer(1L, "Panaceu"));
    }

    @Test
    void addTreatment() {
        when(mapper.dtoToTreatment(treatmentDto)).thenReturn(treatment);
        when(repository.save(treatment)).thenReturn(treatment);
        service.addTreatment(treatmentDto);
    }

    @Test
    void getTreatment() {
        when(repository.findById(treatmentDto.getId())).thenReturn(Optional.ofNullable(treatment));
        when(repository.findById(nonExistentTreatmentDto.getId())).thenThrow(new NotFoundException("treatment"));
        assertThrows(NotFoundException.class, () -> {
            service.getTreatment(nonExistentTreatmentDto.getId());
        });
        assertEquals(service.getTreatment(treatmentDto.getId()).getId(), treatment.getId());
        assertEquals(service.getTreatment(treatmentDto.getId()).getName(), treatment.getName());
    }

    @Test
    void updateTreatment() {
        when(repository.existsById(treatment.getId())).thenReturn(true);
        when(repository.existsById(nonExistingTreatment.getId())).thenReturn(false);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToTreatment(treatmentDto)).thenReturn(treatment);
        when(mapper.dtoToTreatment(nonExistentTreatmentDto)).thenReturn(nonExistingTreatment);
        assertTrue(service.updateTreatment(treatmentDto.getId(), treatmentDto));
        assertFalse(service.updateTreatment(nonExistentTreatmentDto.getId(), nonExistentTreatmentDto));
        assertFalse(service.updateTreatment(4L, treatmentDto));
    }

    @Test
    void addDrug() {
        when(repository.findById(treatmentDto.getId())).thenReturn(Optional.ofNullable(treatment));
        when(drugService.getDrug(existingDrug.getId())).thenReturn(existingDrug);
        when(drugService.getDrug(nonExistingDrug.getId())).thenThrow(new NotFoundException("drug"));
        treatment.setDrugs(new ArrayList<>());
        service.addDrug(treatment.getId(), existingDrug.getId());
        assertThrows(NotFoundException.class, () -> {
            service.addDrug(treatment.getId(), nonExistingDrug.getId());
        });
    }

    @Test
    void removeTreatment() {
        when(repository.findById(treatmentDto.getId())).thenReturn(Optional.ofNullable(treatment));
        when(repository.findById(nonExistentTreatmentDto.getId())).thenThrow(new NotFoundException("treatment"));
        assertThrows(NotFoundException.class, () -> {
            service.removeTreatment(nonExistentTreatmentDto.getId());
        });
        service.removeTreatment(treatmentDto.getId());
    }

    @Test
    void removeDrug() {
        when(repository.findById(treatment.getId())).thenReturn(Optional.ofNullable(treatment));
        when(drugService.getDrug(existingDrug.getId())).thenReturn(existingDrug);
        when(drugService.getDrug(nonExistingDrug.getId())).thenThrow(new NotFoundException("drug"));
        treatment.setDrugs(new ArrayList<>());
        treatment.addDrug(existingDrug);
        service.removeDrug(treatment.getId(), existingDrug.getId());
        assertThrows(NotFoundException.class, () -> {
            service.removeDrug(treatment.getId(), nonExistingDrug.getId());
        });
    }
}
