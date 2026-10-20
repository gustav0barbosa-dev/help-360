package com.help.repository;

import com.help.model.ApontamentoDeTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApontamentoDeTrabalhoRepository extends JpaRepository<ApontamentoDeTrabalho, Long> {

    // RN0041 - Verifica se o usuário já tem trabalho aberto (um por vez)
    Optional<ApontamentoDeTrabalho> findByUsuarioIdAndDataFimIsNull(Long usuarioId);

    // RF0053 - Listar apontamentos por ticket
    List<ApontamentoDeTrabalho> findByTicketId(Long ticketId);

    // Para verificar se um ticket possui algum apontamento ativo
    boolean existsByTicketIdAndDataFimIsNull(Long ticketId);
}