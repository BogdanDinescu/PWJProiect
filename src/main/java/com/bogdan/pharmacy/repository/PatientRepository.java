package com.bogdan.pharmacy.repository;

import com.bogdan.pharmacy.model.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient, Long> {

}
