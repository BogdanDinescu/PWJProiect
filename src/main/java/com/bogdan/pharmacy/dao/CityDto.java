package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class CityDto {
    private Long id;
    @NotBlank
    private String name;

    public CityDto() {
    }

    public CityDto(Long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CityDto cityDto = (CityDto) obj;
        return id.equals(cityDto.id) &&
                Objects.equals(name, cityDto.name);
    }
}
