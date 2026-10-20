package com.help.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_sistema")
@Getter @Setter
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome; // SIGEPREV...

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "sistema")
    private List<Categoria> categorias = new ArrayList<>();

    public String getNome(){
        return nome;
    }
}