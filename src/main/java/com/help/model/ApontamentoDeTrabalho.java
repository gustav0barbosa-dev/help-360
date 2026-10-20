package com.help.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.help.model.enums.*;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tb_apontamento_trabalho")
@Getter @Setter
public class ApontamentoDeTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim; // Nula enquanto em andamento

    @Column(name = "tempo_gasto_minutos")
    private Long tempoGastoMinutos; // RN0043

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_trabalho", nullable = false)
    private TipoTrabalho tipoTrabalho;

    @Column(length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}