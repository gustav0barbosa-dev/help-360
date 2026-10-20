package com.help.repository;

import com.help.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    // RN0051 - Filtro de área por empresa
    List<Area> findByEmpresaIdAndAtivoTrue(Long empresaId);
    
    boolean existsByNomeAndEmpresaId(String nome, Long empresaId);
}