package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.TreatmentMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Treatment;
import com.bogdan.pharmacy.repository.TreatmentRepository;
import com.bogdan.pharmacy.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TreatmentService {
    private final TreatmentRepository repository;
    private final TreatmentMapper mapper;
    private final DrugService drugService;

    public TreatmentService(@Autowired TreatmentRepository repository, @Autowired TreatmentMapper mapper, @Autowired DrugService drugService) {
        this.repository = repository;
        this.mapper = mapper;
        this.drugService = drugService;
    }

    public Treatment getTreatment(Long id) {
        Optional<Treatment> treatment = repository.findById(id);
        if (treatment.isEmpty()) {
            throw new NotFoundException("treatment");
        }
        return treatment.get();
    }

    public Treatment addTreatment(TreatmentDto treatment) {
        treatment.setId(null);
        return repository.save(mapper.dtoToTreatment(treatment));
    }

    public Iterable<Treatment> getTreatments(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public void addDrugs(Long treatment_id, List<Long> ids) {
        Iterable<Drug> drugs = drugService.getDrugs(ids);
        Treatment treatment = getTreatment(treatment_id);
        treatment.addDrugs(MyUtils.iterableToCollections(drugs));
        repository.save(treatment);
    }

    public void addDrug(Long treatment_id, Long drug_id) {
        Drug drug = drugService.getDrug(drug_id);
        Treatment treatment = getTreatment(treatment_id);
        treatment.addDrug(drug);
        repository.save(treatment);
    }

    public boolean updateTreatment(Long id, TreatmentDto treatmentDto) {
        boolean exists = repository.existsById(id);
        Treatment treatment = mapper.dtoToTreatment(treatmentDto);
        treatment.setId(id);
        repository.save(treatment);
        return exists;
    }

    public void removeTreatment(Long id) {
        Treatment treatment = getTreatment(id);
        repository.delete(treatment);
    }

    public void removeDrugs(Long treatment_id, List<Long> ids) {
        Iterable<Drug> drugs = drugService.getDrugs(ids);
        Treatment treatment = getTreatment(treatment_id);
        treatment.removeDrugs(MyUtils.iterableToCollections(drugs));
        repository.save(treatment);
    }

    public void removeDrug(Long treatment_id, Long drug_id) {
        Drug drug = drugService.getDrug(drug_id);
        Treatment treatment = getTreatment(treatment_id);
        treatment.removeDrug(drug);
        repository.save(treatment);
    }
}
