package com.help.dto.response;

import com.help.model.enums.ClassificacaoTicket;
import com.help.model.enums.Prioridade;
import com.help.model.enums.StatusTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class TicketResumoDTO {
    private Long id;
    private Prioridade prioridade;
    private String titulo;
    private LocalDateTime dataCriacao;
    private String nomeSistema;
    private StatusTicket status;
    private String nomeCategoria;
    private String nomeEmpresa;
    private String nomeResponsavel;
    private LocalDateTime previsaoSolucao;
    private LocalDateTime dataSolucao;
    private ClassificacaoTicket classificacao;
}