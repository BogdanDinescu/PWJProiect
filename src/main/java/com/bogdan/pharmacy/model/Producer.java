package com.bogdan.pharmacy.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "producer")
public class Producer {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "producer")
    private List<Drug> drugs;

    public Producer() {
    }

    public Producer(Long id, String name) {
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
}
