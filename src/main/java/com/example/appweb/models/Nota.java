package com.example.appweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import javax.swing.text.StyledEditorKit;

@Entity
@Table(name = "nota")
public class Nota {

    @Id
    @SequenceGenerator(
            name = "actividad_sequence",
            sequenceName = "actividad_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "actividad_sequence"
    )
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(
            name = "actividad_id",
            foreignKey = @ForeignKey(
                    name = "fk_nota_actividad1",
                    foreignKeyDefinition = "FOREIGN KEY (`actividad_id`) REFERENCES `tarea2`.`actividad` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Actividad actividad;

    @NotNull
    private int nota;

    public int getNota() {
        return nota;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public Long getId() {
        return id;
    }

    public Nota(){}
    public Nota(Actividad actividad,
                int nota){
        this.actividad = actividad;
        this.nota = nota;
    }


    public static Boolean validate_nota(int n){
        return (n>=1 && n<=7);
    }
}
