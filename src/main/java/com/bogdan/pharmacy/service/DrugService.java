package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.DrugMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrugService {
    private final DrugRepository repository;
    private final DrugMapper mapper;

    public DrugService(@Autowired DrugRepository repository, @Autowired DrugMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Drug getDrug(Long id) {
        Optional<Drug> drug = repository.findById(id);
        if (drug.isEmpty()) {
            throw new NotFoundException("drug");
        }
        return drug.get();
    }

    public Drug addDrug(DrugDto drug) {
        drug.setId(null);
        return repository.save(mapper.dtoToDrug(drug));
    }

    public boolean updateDrug(Long id, DrugDto drugDto) {
        boolean exists = repository.existsById(id);
        Drug drug = mapper.dtoToDrug(drugDto);
        drug.setId(id);
        repository.save(drug);
        return exists;
    }

    public Iterable<Drug> getDrugs(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public void removeDrug(Long id) {
        Drug drug = getDrug(id);
        repository.delete(drug);
    }


}
