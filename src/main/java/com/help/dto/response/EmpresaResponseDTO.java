package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class EmpresaResponseDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
}