package com.oriontek.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("cliente")
    private List<Direccion> direcciones;
}
