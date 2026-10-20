package com.help.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AreaRequestDTO {
    @NotBlank @Size(max = 100)
    private String nome;

    @NotNull(message = "Empresa é obrigatória") // RN0051
    private Long empresaId;
}