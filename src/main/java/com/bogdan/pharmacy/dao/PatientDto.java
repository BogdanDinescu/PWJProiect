package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PatientDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private Long cityId;

    public PatientDto() {
    }

    public PatientDto(Long id, String name, Long cityId) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PatientDto patientDto = (PatientDto) obj;
        return id.equals(patientDto.id) &&
                Objects.equals(name, patientDto.name) &&
                cityId.equals(patientDto.cityId);
    }
}
