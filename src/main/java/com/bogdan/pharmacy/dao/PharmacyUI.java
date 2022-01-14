package com.bogdan.pharmacy.dao;

import javax.validation.constraints.NotBlank;

public class PharmacyUI {
    private Long id;
    @NotBlank(message = "Address must not be blank")
    private String address;
    @NotBlank(message = "City id must not be null")
    private String city;

    public PharmacyUI(Long id, String address, String city) {
        this.id = id;
        this.address = address;
        this.city = city;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
