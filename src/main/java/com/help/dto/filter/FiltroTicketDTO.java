package com.help.dto.filter;

import com.help.model.enums.ClassificacaoTicket;
import com.help.model.enums.Prioridade;
import com.help.model.enums.StatusTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class FiltroTicketDTO {

    private Long id;
    private String titulo;
    private Prioridade prioridade;
    private Long empresaId;
    private Long sistemaId;
    private StatusTicket status;
    private Long categoriaId;
    private Long responsavelId;
    private Long solicitanteId;
    private ClassificacaoTicket classificacao;
    private LocalDate dataCriacaoInicio;
    private LocalDate dataCriacaoFim;

    // Paginação (padrão 20 itens por página - RNF0016)
    private Integer pagina = 0;
    private Integer tamanho = 20;
    private String ordenacao = "dataCriacao";
    private String direcao = "DESC";
}