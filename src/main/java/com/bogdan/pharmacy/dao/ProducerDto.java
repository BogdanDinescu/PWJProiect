package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class ProducerDto {
    private Long id;
    @NotBlank
    private String name;

    public ProducerDto() {
    }

    public ProducerDto(Long id, String name) {
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
        ProducerDto producerDto = (ProducerDto) obj;
        return id.equals(producerDto.id) &&
                Objects.equals(name, producerDto.name);
    }
}
