package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.PatientDto;
import com.bogdan.pharmacy.model.City;
import com.bogdan.pharmacy.model.Patient;
import com.bogdan.pharmacy.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {
    private final CityService cityService;

    public PatientMapper(@Autowired CityService cityService) {
        this.cityService = cityService;
    }

    public PatientDto patientToDto(Patient patient) {
        return new PatientDto(patient.getId(), patient.getName(), patient.getCity().getId());
    }

    public Patient dtoToPatient(PatientDto patient) {
        City city = cityService.getCity(patient.getCityId());
        return new Patient(patient.getId(), patient.getName(), city);
    }
}
