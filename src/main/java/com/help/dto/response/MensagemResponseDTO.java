package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class MensagemResponseDTO {
    private Long id;
    private String texto;
    private LocalDateTime dataEnvio;
    private Boolean publico;
    private String nomeAutor;
}