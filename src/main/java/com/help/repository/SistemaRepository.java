package com.help.repository;

import com.help.model.Sistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SistemaRepository extends JpaRepository<Sistema, Long> {

    Optional<Sistema> findByNome(String nome);
    boolean existsByNome(String nome);
    List<Sistema> findByAtivoTrue();
}