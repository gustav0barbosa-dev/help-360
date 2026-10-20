package com.help.repository;

import com.help.model.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Long> {

    List<Anexo> findByTicketId(Long ticketId);
    
    // Para validação na exclusão (RF0044)
    boolean existsByIdAndAutorId(Long id, Long autorId);
}