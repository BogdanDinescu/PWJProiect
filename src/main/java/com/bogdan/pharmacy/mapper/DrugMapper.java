package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.DrugDto;
import com.bogdan.pharmacy.model.Drug;
import com.bogdan.pharmacy.model.Producer;
import com.bogdan.pharmacy.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DrugMapper {
    private final ProducerService producerService;

    public DrugMapper(@Autowired ProducerService producerService) {
        this.producerService = producerService;
    }

    public DrugDto drugToDto(Drug drug) {
        return new DrugDto(drug.getId(), drug.getName(), drug.getProducer().getId());
    }

    public Drug dtoToDrug(DrugDto drug) {
        Producer producer = producerService.getProducer(drug.getProducerId());
        return new Drug(drug.getId(), drug.getName(), producer);
    }

    public List<DrugDto> drugListToDtoList(List<Drug> drugs) {
        return drugs.stream().map(this::drugToDto).collect(Collectors.toList());
    }
}
