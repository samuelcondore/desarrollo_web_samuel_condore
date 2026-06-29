package com.example.appweb.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Formatter;

@Entity
@Table(name = "foto")
public class Foto {
    @Id
    @SequenceGenerator(
            name = "foto_sequence",
            sequenceName = "foto_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "foto_sequence"
    )
    private Long id;

    @NotNull
    private String ruta_archivo;

    @NotNull
    private String nombre_archivo;

    @NotNull
    @OneToOne
    @JoinColumn(
            name = "actividad_id",
            foreignKey = @ForeignKey(
                    name = "fk_foto_actividad1",
                    foreignKeyDefinition = "FOREIGN KEY (`actividad_id`) REFERENCES `tarea2`.`actividad` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Actividad actividad;

    public Foto() {
    }

    public Foto(String ruta_archivo,
                String nombre_archivo,
                Actividad actividad) {
        this.ruta_archivo = ruta_archivo;
        this.nombre_archivo = nombre_archivo;
        this.actividad = actividad;
    }

    public Long getId() {
        return id;
    }
    public String getRuta_archivo(){return ruta_archivo;}
    public String getNombre_archivo(){return nombre_archivo;}
    public Actividad getActividad(){return actividad;}
}
