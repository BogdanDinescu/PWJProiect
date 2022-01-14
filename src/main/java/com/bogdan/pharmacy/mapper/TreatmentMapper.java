package com.bogdan.pharmacy.mapper;

import com.bogdan.pharmacy.dao.TreatmentDto;
import com.bogdan.pharmacy.model.Treatment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TreatmentMapper {
    public TreatmentDto treatmentToDto(Treatment treatment) {
        return new TreatmentDto(treatment.getId(), treatment.getName());
    }

    public Treatment dtoToTreatment(TreatmentDto treatment) {
        return new Treatment(treatment.getId(), treatment.getName());
    }

    public List<TreatmentDto> treatmentListToDtoList(List<Treatment> treatments) {
        return treatments.stream().map(this::treatmentToDto).collect(Collectors.toList());
    }
}
