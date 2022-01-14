package com.bogdan.pharmacy.repository;

import com.bogdan.pharmacy.model.Treatment;
import org.springframework.data.repository.CrudRepository;

public interface TreatmentRepository extends CrudRepository<Treatment, Long> {

}