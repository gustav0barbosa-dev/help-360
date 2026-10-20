package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
    private Long sistemaId;
    private String nomeSistema;
}