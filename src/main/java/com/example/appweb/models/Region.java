package com.example.appweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "region")
public class Region {

    @Id
    @SequenceGenerator(
            name = "region_sequence",
            sequenceName = "region_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "region_sequence"
    )
    private Long id;

    @NotNull
    private String nombre;

    public Region(){}

    public Region(String nombre) {
        this.nombre = nombre;
    }

    public Long getId(){return id;}
    public String getNombre(){return nombre;}
}
