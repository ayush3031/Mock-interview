package com.mockinterview.backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "company",cascade = CascadeType.ALL)
    private List<Question>questions;

    public Company(){}
    public Company(String name)
    {
        this.name=name;
    }

}
