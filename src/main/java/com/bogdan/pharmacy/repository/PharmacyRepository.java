package com.bogdan.pharmacy.repository;

import com.bogdan.pharmacy.model.Pharmacy;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PharmacyRepository extends CrudRepository<Pharmacy, Long> {

    List<Pharmacy> findAll();
}
