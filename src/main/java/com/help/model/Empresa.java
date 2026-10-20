package com.help.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_empresa")
@Getter @Setter
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome; // DBIP, DIPM, DFIN, DTR, DBPE...

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "empresa")
    private List<Area> areas = new ArrayList<>();
}
