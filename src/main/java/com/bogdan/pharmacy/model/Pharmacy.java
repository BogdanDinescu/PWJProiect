package com.bogdan.pharmacy.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "pharmacy")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String address;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Drug> drugs;

    public Pharmacy() {
    }

    public Pharmacy(Long id, String address, City city) {
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }

    public void addDrug(Drug drug) {
        drugs.add(drug);
    }

    public void addDrugs(Collection<Drug> drugs) {
        this.drugs.addAll(drugs);
    }

    public void removeDrug(Drug drug) {
        drugs.remove(drug);
    }

    public void removeDrugs(Collection<Drug> drugs) {
        this.drugs.removeAll(drugs);
    }
}
