package com.bogdan.pharmacy.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "treatment")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToMany(mappedBy = "treatments", fetch = FetchType.LAZY)
    private List<Patient> patients;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Drug> drugs;

    public Treatment() {
    }

    public Treatment(Long id, String name) {
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

    public List<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
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
