package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.repository.DrugRepository;
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
public class DrugServiceTest {
    @InjectMocks
    private DrugService service;
    @Mock
    private DrugRepository repository;
    @Mock
    private DrugMapper mapper;

    static DrugDto drugDto;
    static Drug drug;
    static DrugDto nonExistentDrugDto;
    static Drug nonExistingDrug;
    static DrugDto drugWithNonExitingProducerDto;
    static Drug drugWithNonExistingProducer;

    @BeforeEach
    public void setup() {
        drugDto = new DrugDto(1L, "Medicament", 1L);
        drug = new Drug(1L, "Medicament", new Producer(1L, "Producator"));
        nonExistentDrugDto = new DrugDto(2L, "Medicament2", 1L);
        nonExistingDrug = new Drug(2L, "Medicament2", new Producer(1L, "Producator"));
        drugWithNonExistingProducer = new Drug(3L, "Medicament3", new Producer(2L, "Producator fals"));
        drugWithNonExitingProducerDto = new DrugDto(3L, "Medicament3", 2L);
    }

    @Test
    void addDrug() {
        when(mapper.dtoToDrug(drugWithNonExitingProducerDto)).thenThrow(new NotFoundException("producer"));
        when(mapper.dtoToDrug(drugDto)).thenReturn(drug);
        when(repository.save(drug)).thenReturn(drug);
        assertThrows(NotFoundException.class, () -> {service.addDrug(drugWithNonExitingProducerDto);});
        service.addDrug(drugDto);
    }

    @Test
    void getDrug() {
        when(repository.findById(drugDto.getId())).thenReturn(Optional.ofNullable(drug));
        when(repository.findById(nonExistentDrugDto.getId())).thenThrow(new NotFoundException("drug"));
        assertThrows(NotFoundException.class, () -> {service.getDrug(nonExistentDrugDto.getId());});
        assertEquals(service.getDrug(drugDto.getId()).getId(), drug.getId());
        assertEquals(service.getDrug(drugDto.getId()).getName(), drug.getName());
        assertEquals(service.getDrug(drugDto.getId()).getProducer().getId(), drug.getProducer().getId());
    }

    @Test
    void updateDrug() {
        when(repository.existsById(drug.getId())).thenReturn(true);
        when(repository.existsById(nonExistingDrug.getId())).thenReturn(false);
        when(repository.existsById(drugWithNonExistingProducer.getId())).thenReturn(true);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToDrug(drugDto)).thenReturn(drug);
        when(mapper.dtoToDrug(nonExistentDrugDto)).thenReturn(nonExistingDrug);
        when(mapper.dtoToDrug(drugWithNonExitingProducerDto)).thenThrow(new NotFoundException("producer"));
        assertThrows(NotFoundException.class, () -> {service.updateDrug(3L, drugWithNonExitingProducerDto); });
        assertTrue(service.updateDrug(drugDto.getId(), drugDto));
        assertFalse(service.updateDrug(nonExistentDrugDto.getId(), nonExistentDrugDto));
        assertFalse(service.updateDrug(4L, drugDto));
    }

    @Test
    void removeDrug() {
        when(repository.findById(drugDto.getId())).thenReturn(Optional.ofNullable(drug));
        when(repository.findById(nonExistentDrugDto.getId())).thenThrow(new NotFoundException("drug"));
        assertThrows(NotFoundException.class, () -> {service.removeDrug(nonExistentDrugDto.getId());});
        service.removeDrug(drugDto.getId());
    }

}
