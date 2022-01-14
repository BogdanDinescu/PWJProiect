package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PharmacyDto {
    private Long id;
    @NotBlank(message = "Address must not be blank")
    private String address;
    @NotNull(message = "City id must not be null")
    private Long cityId;

    public PharmacyDto() {
    }

    public PharmacyDto(Long id, String address, Long cityId) {
        this.id = id;
        this.address = address;
        this.cityId = cityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        PharmacyDto pharmacyDto = (PharmacyDto) obj;
        return id.equals(pharmacyDto.id) &&
                Objects.equals(address, pharmacyDto.address) &&
                cityId.equals(pharmacyDto.cityId);
    }
}
