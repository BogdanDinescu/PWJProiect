package com.bogdan.pharmacy.model;


import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Treatment> treatments;

    public Patient() {
    }

    public Patient(Long id, String name, City city) {
        this.id = id;
        this.name = name;
        this.city = city;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public void addTreatment(Treatment treatment) {
        treatments.add(treatment);
    }

    public void removeTreatment(Treatment treatment) {
        treatments.remove(treatment);
    }

    public void addTreatments(Collection<Treatment> treatments) {
        this.treatments.addAll(treatments);
    }

    public void removeTreatments(Collection<Treatment> treatments) {
        this.treatments.removeAll(treatments);
    }
}
