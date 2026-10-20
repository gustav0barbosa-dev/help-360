package com.help.dto.request;

import com.help.model.enums.TipoTrabalho;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApontamentoRequestDTO {

    @NotNull(message = "Ticket é obrigatório")
    private Long ticketId;

    @NotNull(message = "Tipo de trabalho é obrigatório")
    private TipoTrabalho tipoTrabalho;

    private String descricao;
}