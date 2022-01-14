package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.PharmacyDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.repository.PharmacyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PharmacyServiceTest {
    @InjectMocks
    private PharmacyService service;
    @Mock
    private PharmacyRepository repository;
    @Mock
    private PharmacyMapper mapper;
    @Mock
    private DrugService drugService;

    static PharmacyDto pharmacyDto;
    static Pharmacy pharmacy;
    static PharmacyDto nonExistentPharmacyDto;
    static Pharmacy nonExistingPharmacy;
    static PharmacyDto pharmacyWithNonExitingCityDto;
    static Pharmacy pharmacyWithNonExistingCity;
    static Drug existingDrug;
    static Drug nonExistingDrug;

    @BeforeEach
    public void setup() {
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
    void addPharmacy() {
        when(mapper.dtoToPharmacy(pharmacyWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        when(mapper.dtoToPharmacy(pharmacyDto)).thenReturn(pharmacy);
        when(repository.save(pharmacy)).thenReturn(pharmacy);
        assertThrows(NotFoundException.class, () -> {service.addPharmacy(pharmacyWithNonExitingCityDto);});
        service.addPharmacy(pharmacyDto);
    }

    @Test
    void getPharmacy() {
        when(repository.findById(pharmacyDto.getId())).thenReturn(Optional.ofNullable(pharmacy));
        when(repository.findById(nonExistentPharmacyDto.getId())).thenThrow(new NotFoundException("pharmacy"));
        assertThrows(NotFoundException.class, () -> {service.getPharmacy(nonExistentPharmacyDto.getId());});
        assertEquals(service.getPharmacy(pharmacyDto.getId()).getId(), pharmacy.getId());
        assertEquals(service.getPharmacy(pharmacyDto.getId()).getAddress(), pharmacy.getAddress());
        assertEquals(service.getPharmacy(pharmacyDto.getId()).getCity().getId(), pharmacy.getCity().getId());
    }

    @Test
    void getPharmacies() {
        when(repository.findAll()).thenReturn(List.of(pharmacy));
        assertEquals(service.getPharmacies().get(0), pharmacy);
    }

    @Test
    void updatePharmacy() {
        when(repository.existsById(pharmacy.getId())).thenReturn(true);
        when(repository.existsById(nonExistingPharmacy.getId())).thenReturn(false);
        when(repository.existsById(pharmacyWithNonExistingCity.getId())).thenReturn(true);
        when(repository.existsById(4L)).thenReturn(false);
        when(mapper.dtoToPharmacy(pharmacyDto)).thenReturn(pharmacy);
        when(mapper.dtoToPharmacy(nonExistentPharmacyDto)).thenReturn(nonExistingPharmacy);
        when(mapper.dtoToPharmacy(pharmacyWithNonExitingCityDto)).thenThrow(new NotFoundException("city"));
        assertThrows(NotFoundException.class, () -> {service.updatePharmacy(3L, pharmacyWithNonExitingCityDto); });
        assertTrue(service.updatePharmacy(pharmacyDto.getId(), pharmacyDto));
        assertFalse(service.updatePharmacy(nonExistentPharmacyDto.getId(), nonExistentPharmacyDto));
        assertFalse(service.updatePharmacy(4L, pharmacyDto));
    }

    @Test
    void addDrug() {
        when(repository.findById(pharmacyDto.getId())).thenReturn(Optional.ofNullable(pharmacy));
        when(drugService.getDrug(existingDrug.getId())).thenReturn(existingDrug);
        when(drugService.getDrug(nonExistingDrug.getId())).thenThrow(new NotFoundException("drug"));
        pharmacy.setDrugs(new ArrayList<>());
        service.addDrug(pharmacy.getId(), existingDrug.getId());
        assertThrows(NotFoundException.class, () -> {service.addDrug(pharmacy.getId(), nonExistingDrug.getId());});
    }

    @Test
    void removePharmacy() {
        when(repository.findById(pharmacyDto.getId())).thenReturn(Optional.ofNullable(pharmacy));
        when(repository.findById(nonExistentPharmacyDto.getId())).thenThrow(new NotFoundException("pharmacy"));
        assertThrows(NotFoundException.class, () -> {service.removePharmacy(nonExistentPharmacyDto.getId());});
        service.removePharmacy(pharmacyDto.getId());
    }

    @Test
    void removeDrug() {
        when(repository.findById(pharmacy.getId())).thenReturn(Optional.ofNullable(pharmacy));
        when(drugService.getDrug(existingDrug.getId())).thenReturn(existingDrug);
        when(drugService.getDrug(nonExistingDrug.getId())).thenThrow(new NotFoundException("drug"));
        pharmacy.setDrugs(new ArrayList<>());
        pharmacy.addDrug(existingDrug);
        service.removeDrug(pharmacy.getId(), existingDrug.getId());
        assertThrows(NotFoundException.class, () -> {service.removeDrug(pharmacy.getId(), nonExistingDrug.getId());});
    }

}