package com.help.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.help.model.Ticket;
import com.help.model.enums.StatusTicket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    // Adiciona o EntityGraph para trazer as relações de uma vez
    @EntityGraph(attributePaths = {"empresa", "area", "sistema", "categoria", "solicitante", "responsavel"})
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdComRelacionamentos(@Param("id") Long id);

    @EntityGraph(attributePaths = {"empresa", "area", "sistema", "categoria", "solicitante", "responsavel"})
    @Query("SELECT t FROM Ticket t WHERE t.status NOT IN :status")
    List<Ticket> findByStatusNotInComRelacionamentos(@Param("status") List<StatusTicket> status);

    @EntityGraph(attributePaths = {"empresa", "area", "sistema", "categoria", "solicitante", "responsavel"})
    @Query("SELECT t FROM Ticket t WHERE t.solicitante.id = :usuarioId OR t.responsavel.id = :usuarioId")
    List<Ticket> findMeusTicketsComRelacionamentos(@Param("usuarioId") Long usuarioId);

    // ===== RELATÓRIOS =====

    // RF0071 - Relatório por período
    @Query("SELECT t.categoria.nome, COUNT(t), SUM(CASE WHEN t.status IN ('RESOLVIDO', 'FECHADO') THEN 1 ELSE 0 END) " +
           "FROM Ticket t WHERE t.dataCriacao BETWEEN :inicio AND :fim " +
           "GROUP BY t.categoria.nome")
    List<Object[]> gerarRelatorioPorPeriodo(
            @Param("inicio") LocalDateTime inicio, 
            @Param("fim") LocalDateTime fim);

    // RF0072 - Tempo médio por responsável
    @Query("SELECT t.responsavel.nome, AVG(TIMESTAMPDIFF(MINUTE, t.dataCriacao, t.dataSolucao)) " +
           "FROM Ticket t WHERE t.status IN ('RESOLVIDO', 'FECHADO') AND t.responsavel IS NOT NULL " +
           "GROUP BY t.responsavel.nome")
    List<Object[]> calcularTempoMedioPorResponsavel();

}