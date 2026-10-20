package com.help.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MensagemRequestDTO {

    @NotBlank(message = "Texto da mensagem é obrigatório")
    private String texto;

    @NotNull(message = "É necessário informar se é pública ou interna")
    private Boolean publico; // RN0031
}