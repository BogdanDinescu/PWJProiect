package com.bogdan.pharmacy.service;

import com.bogdan.pharmacy.dao.PharmacyDto;
import com.bogdan.pharmacy.exception.NotFoundException;
import com.bogdan.pharmacy.mapper.PharmacyMapper;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Pharmacy;
import com.bogdan.pharmacy.repository.PharmacyRepository;
import com.bogdan.pharmacy.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PharmacyService {
    private final PharmacyRepository repository;
    private final PharmacyMapper mapper;
    private final DrugService drugService;

    public PharmacyService(@Autowired PharmacyRepository repository, @Autowired PharmacyMapper mapper, @Autowired DrugService drugService) {
        this.repository = repository;
        this.mapper = mapper;
        this.drugService = drugService;
    }

    public List<Pharmacy> getPharmacies() {
        return repository.findAll();
    }

    public Pharmacy addPharmacy(PharmacyDto pharmacy) {
        pharmacy.setId(null);
        return repository.save(mapper.dtoToPharmacy(pharmacy));
    }

    public Pharmacy getPharmacy(Long id) {
        Optional<Pharmacy> pharmacy = repository.findById(id);
        if (pharmacy.isEmpty()) {
            throw new NotFoundException("pharmacy");
        }
        return pharmacy.get();
    }

    public boolean updatePharmacy(Long id, PharmacyDto pharmacyDto) {
        boolean exists = repository.existsById(id);
        Pharmacy pharmacy = mapper.dtoToPharmacy(pharmacyDto);
        pharmacy.setId(id);
        repository.save(pharmacy);
        return exists;
    }

    public void addDrug(Long pharmacy_id, Long drug_id) {
       Drug drug = drugService.getDrug(drug_id);
       Pharmacy pharmacy = getPharmacy(pharmacy_id);
       pharmacy.addDrug(drug);
       repository.save(pharmacy);
    }

    public void addDrugs(Long pharmacy_id, List<Long> ids) {
        Iterable<Drug> drugs = drugService.getDrugs(ids);
        Pharmacy pharmacy = getPharmacy(pharmacy_id);
        pharmacy.addDrugs(MyUtils.iterableToCollections(drugs));
        repository.save(pharmacy);
    }

    public void removePharmacy(Long id) {
        Pharmacy pharmacy = getPharmacy(id);
        repository.delete(pharmacy);
    }

    public void removeDrug(Long pharmacy_id, Long drug_id) {
        Drug drug = drugService.getDrug(drug_id);
        Pharmacy pharmacy = getPharmacy(pharmacy_id);
        pharmacy.removeDrug(drug);
        repository.save(pharmacy);
    }

    public void removeDrugs(Long pharmacy_id, List<Long> ids) {
        Iterable<Drug> drugs = drugService.getDrugs(ids);
        Pharmacy pharmacy = getPharmacy(pharmacy_id);
        pharmacy.removeDrugs(MyUtils.iterableToCollections(drugs));
        repository.save(pharmacy);
    }
}
