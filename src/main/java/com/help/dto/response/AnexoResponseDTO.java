package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class AnexoResponseDTO {
    private Long id;
    private String nomeArquivo;
    private String urlArquivo;
    private String tipoArquivo;
    private Long tamanhoBytes;
    private LocalDateTime dataUpload;
    private String nomeAutor;
}