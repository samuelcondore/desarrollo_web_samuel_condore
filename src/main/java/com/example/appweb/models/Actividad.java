package com.example.appweb.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "actividad")
public class Actividad {

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
    @ManyToOne
    @JoinColumn(
            name = "miembro_id",
            foreignKey = @ForeignKey(
                    name = "fk_actividad_miembro1",
                    foreignKeyDefinition = "FOREIGN KEY (`miembro_id`) REFERENCES `tarea2`.`miembro` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Miembro miembro;

    @NotNull
    @Column(name = "dia", columnDefinition = "ENUM('lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado', 'domingo')")
    private String dia;

    @NotNull
    private String hora_inicio;

    @NotNull
    private String duracion;

    @NotNull
    @Column(name = "tipo", columnDefinition = "ENUM('arte', 'deporte', 'tecnología', 'social', 'recreación', 'otra')")
    private String tipo;

    @NotNull
    private String nombre;

    @NotNull
    private String descripcion;

    public Actividad(){}

    public Actividad(Miembro miembro,
                     String dia,
                     String hora_inicio,
                     String duracion,
                     String tipo,
                     String nombre,
                     String descripcion) {
        this.miembro = miembro;
        this.dia = dia;
        this.hora_inicio = hora_inicio;
        this.duracion = duracion;
        this.tipo = tipo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId(){return id;}
    public Miembro getMiembro(){return miembro;}
    public String getDia(){return dia;}
    public String getHora_inicio(){return hora_inicio;}
    public String getDuracion(){return duracion;}
    public String getTipo(){return tipo;}
    public String getNombre(){return nombre;}
    public String getDescripcion(){return descripcion;}

    public static Boolean validate_register_act(String nombre,
                                                String tipo,
                                                String dia,
                                                String horai,
                                                String duracion) {
        return (validate_nombre_act(nombre) && validate_tipo(tipo)
        && validate_dia(dia) && validate_horai(horai) && validate_duracion(duracion));
    }

    public static Boolean validate_nombre_act(String nombre) {
        if (nombre == null) {
            return false;
        }
        int len = nombre.length();
        return len>=1 && len<=44;
    }
    public static Boolean validate_tipo(String tipo) {
        return tipo != null;
    }
    public static Boolean validate_dia(String dia) {
        return dia != null;
    }
    public static Boolean validate_horai(String horai) {
        return horai != null;
    }
    public static Boolean validate_duracion(String duracion) {
        return duracion != null;
    }
}
