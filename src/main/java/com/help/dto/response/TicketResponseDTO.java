package com.help.dto.response;

import com.help.model.enums.ClassificacaoTicket;
import com.help.model.enums.Prioridade;
import com.help.model.enums.StatusTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class TicketResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Prioridade prioridade;
    private StatusTicket status;
    private ClassificacaoTicket classificacao;

    private LocalDateTime dataCriacao;
    private LocalDateTime previsaoSolucao;
    private LocalDateTime dataSolucao;

    // Nomes em vez de objetos completos (evita loops e exposição)
    private String nomeSolicitante;
    private String nomeResponsavel;
    private String nomeEmpresa;
    private String nomeArea;
    private String nomeSistema;
    private String nomeCategoria;

    private List<TicketResumoDTO> ticketsRelacionados;
    private List<MensagemResponseDTO> mensagens;
    private List<AnexoResponseDTO> anexos;
    private List<ApontamentoResponseDTO> apontamentos;
}