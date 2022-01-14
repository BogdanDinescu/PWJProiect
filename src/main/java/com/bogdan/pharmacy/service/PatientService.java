package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.PatientDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.PatientMapper;
import com.bogdan.pharmacy.model.Patient;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.repository.PatientRepository;
import com.bogdan.pharmacy.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final TreatmentService treatmentService;

    public PatientService(@Autowired PatientRepository repository, @Autowired PatientMapper mapper, @Autowired TreatmentService treatmentService) {
        this.repository = repository;
        this.mapper = mapper;
        this.treatmentService = treatmentService;
    }

    public Patient getPatient(Long id) {
        Optional<Patient> patient = repository.findById(id);
        if (patient.isEmpty()) {
            throw new NotFoundException("patient");
        }
        return patient.get();
    }

    public Patient addPatient(PatientDto patient) {
        patient.setId(null);
        return repository.save(mapper.dtoToPatient(patient));
    }

    public void addTreatment(Long patient_id, Long treatment_id) {
        Patient patient = getPatient(patient_id);
        Treatment treatment = treatmentService.getTreatment(treatment_id);
        patient.addTreatment(treatment);
        repository.save(patient);
    }

    public void addTreatments(Long patient_id, List<Long> ids) {
        Patient patient = getPatient(patient_id);
        Iterable<Treatment> treatments = treatmentService.getTreatments(ids);
        patient.addTreatments(MyUtils.iterableToCollections(treatments));
        repository.save(patient);
    }

    public boolean updatePatient(Long id, PatientDto patientDto) {
        boolean exists = repository.existsById(id);
        Patient patient = mapper.dtoToPatient(patientDto);
        patient.setId(id);
        repository.save(patient);
        return exists;
    }

    public void removePatient(Long id) {
        Patient patient = getPatient(id);
        repository.delete(patient);
    }

    public void removeTreatment(Long patient_id, Long treatment_id) {
        Patient patient = getPatient(patient_id);
        Treatment treatment = treatmentService.getTreatment(treatment_id);
        patient.removeTreatment(treatment);
        repository.save(patient);
    }

    public void removeTreatments(Long patient_id, List<Long> ids) {
        Patient patient = getPatient(patient_id);
        Iterable<Treatment> treatments = treatmentService.getTreatments(ids);
        patient.removeTreatments(MyUtils.iterableToCollections(treatments));
        repository.save(patient);
    }
}
