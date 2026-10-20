package com.help.dto.response;

import com.help.model.enums.TipoTrabalho;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ApontamentoResponseDTO {
    private Long id;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Long tempoGastoMinutos;
    private TipoTrabalho tipoTrabalho;
    private String descricao;
    private String nomeUsuario;
    private String nomeTicket;
}