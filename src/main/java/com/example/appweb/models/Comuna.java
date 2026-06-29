package com.example.appweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "comuna")
public class Comuna {
    @Id
    @SequenceGenerator(
            name = "comuna_sequence",
            sequenceName = "comuna_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comuna_sequence"
    )
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    @ManyToOne
    @JoinColumn(
            name = "region_id",
            foreignKey = @ForeignKey(
                    name = "fk_comuna_region1",
                    foreignKeyDefinition = "FOREIGN KEY (`region_id`) REFERENCES `tarea2`.`region` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Region region;

    public Comuna(){}

    public Comuna(String nombre,
                   Region region) {
        this.nombre = nombre;
        this.region = region;
    }

    public Long getId(){return id;}
    public String getNombre(){return nombre;}
    public Region getRegion(){return region;}
}