package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class DrugDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull(message = "producer id must not be null")
    private Long producerId;

    public DrugDto() {
    }

    public DrugDto(Long id, String name, Long producerId) {
        this.id = id;
        this.name = name;
        this.producerId = producerId;
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

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DrugDto DrugDto = (DrugDto) obj;
        return id.equals(DrugDto.id) &&
                Objects.equals(name, DrugDto.name) &&
                producerId.equals(DrugDto.producerId);
    }
}
