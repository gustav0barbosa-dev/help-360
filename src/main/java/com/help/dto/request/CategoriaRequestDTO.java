package com.help.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class CategoriaRequestDTO {
    @NotBlank @Size(max = 100)
    private String nome;

    @NotNull(message = "Sistema é obrigatório") // RN0052
    private Long sistemaId;
}