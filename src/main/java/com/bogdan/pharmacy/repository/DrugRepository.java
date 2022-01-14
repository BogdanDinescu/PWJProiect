package com.bogdan.pharmacy.repository;

import com.bogdan.pharmacy.model.Drug;
import org.springframework.data.repository.CrudRepository;

public interface DrugRepository extends CrudRepository<Drug, Long> {

}