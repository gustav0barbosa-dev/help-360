package com.help.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.help.model.Mensagem;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    // Para atendentes/admin: todas as mensagens (públicas e internas)
    @EntityGraph(attributePaths = {"autor"}) // <-- Traz o autor junto
    List<Mensagem> findByTicketIdOrderByDataEnvioAsc(Long ticketId);

    // Para solicitantes: apenas mensagens públicas
    @EntityGraph(attributePaths = {"autor"}) // <-- Traz o autor junto
    List<Mensagem> findByTicketIdAndPublicoTrueOrderByDataEnvioAsc(Long ticketId);
}