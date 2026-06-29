package com.example.appweb.models;

import java.util.regex.Pattern;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "miembro")
public class Miembro {
    @Id
    @SequenceGenerator(
            name = "miembro_sequence",
            sequenceName = "miembro_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "miembro_sequence"
    )
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private String email;

    @NotNull
    private String telefono;

    @NotNull
    private LocalDateTime fecha_registro;

    @NotNull
    @ManyToOne
    @JoinColumn(
            name = "comuna_id",
            foreignKey = @ForeignKey(
                    name = "fk_miembro_comuna1",
                    foreignKeyDefinition = "FOREIGN KEY (`comuna_id`) REFERENCES `tarea2`.`comuna` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
            )
    )
    private Comuna comuna;

    public Miembro(){}

    public Miembro(String nombre,
                   String email,
                   String telefono,
                   Comuna comuna) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fecha_registro = LocalDateTime.now();
        this.comuna = comuna;
    }

    public Long getId(){return id;}
    public String getNombre(){return nombre;}
    public String getEmail(){return email;}
    public String getTelefono(){return telefono;}
    public LocalDateTime getFecha_registro(){return fecha_registro;}
    public Comuna getComuna(){return comuna;}

    public static Boolean validate_register_member(String username, String telefono, String email){
        return (validate_username(username) && validate_telefono(telefono) && validate_email(email));
    }
    public static Boolean validate_username(String username){
        if (username == null){return false;}
        return username.matches("^[a-zA-Z]+$");
    }
    public static Boolean validate_telefono(String telefono){
        if (telefono == null){return false;}
        return telefono.matches("^(\\s*\\d\\s*){8}$");
    }
    public static Boolean validate_email(String email){
        if (email == null){return false;}
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
}