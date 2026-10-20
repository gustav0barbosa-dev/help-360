package com.help.repository;

import com.help.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // RN0052 - Filtro de categoria por sistema
    List<Categoria> findBySistemaIdAndAtivoTrue(Long sistemaId);
    
    boolean existsByNomeAndSistemaId(String nome, Long sistemaId);
}