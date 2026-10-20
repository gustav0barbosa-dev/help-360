package com.help.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JustificativaRequestDTO {

    @NotBlank(message = "Justificativa é obrigatória")
    private String justificativa;
}