package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AreaResponseDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
    private Long empresaId;
    private String nomeEmpresa;
}