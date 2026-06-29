package com.example.appweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import javax.accessibility.AccessibleIcon;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @SequenceGenerator(
            name = "comentario_sequence",
            sequenceName = "comentario_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comentario_sequence"
    )
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private String texto;

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    @ManyToOne
    @JoinColumn(
            name = "actividad_id",
            foreignKey = @ForeignKey(
                    name = "fk_comentario_actividad1",
                    foreignKeyDefinition = "FOREIGN KEY (`actividad_id`) REFERENCES `tarea2`.`actividad` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Actividad actividad;

    public Comentario(){}

    public Comentario(String nombre,
                   String texto,
                   Actividad actividad) {
        this.nombre = nombre;
        this.texto = texto;
        this.fecha = LocalDateTime.now();
        this.actividad = actividad;
    }

    public Long getId(){return id;}
    public String getNombre(){return nombre;}
    public String getTexto(){return texto;}
    public LocalDateTime getFecha(){return fecha;}
    public Actividad getActividad(){return actividad;}

    public static Boolean validate_comment(String comment){
        if (comment == null){return false;}
        int len = comment.length();
        return len>=5 && len<=299;
    }
    public static Boolean validate_commenter_name(String name){
        if (name == null){return false;}
        int len = name.length();
        return len>=3 && len<=79;
    }
}
