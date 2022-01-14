package com.bogdan.pharmacy.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "drug")
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "drugs", fetch = FetchType.LAZY)
    private List<Pharmacy> pharmacy;
    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    private Producer producer;
    @ManyToMany(mappedBy = "drugs", fetch = FetchType.LAZY)
    private List<Treatment> treatment;

    public Drug() {
    }

    public Drug(Long id, String name, Producer producer) {
        this.id = id;
        this.name = name;
        this.producer = producer;
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

    public List<Pharmacy> getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(List<Pharmacy> pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public List<Treatment> getTreatment() {
        return treatment;
    }

    public void setTreatment(List<Treatment> treatment) {
        this.treatment = treatment;
    }
}
