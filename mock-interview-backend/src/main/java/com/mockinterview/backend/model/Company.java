package com.mockinterview.backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

@Entity
@Table(name = "Company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "company",cascade = CascadeType.ALL)
    private List<Questions> questions;

    public Company(){}
    public Company(String name)
    {
        this.name=name;
    }

}
