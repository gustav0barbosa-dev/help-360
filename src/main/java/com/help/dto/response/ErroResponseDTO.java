package com.help.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ErroResponseDTO {
    
    private LocalDateTime timestamp;
    private Integer status;         // Ex: 400, 404
    private String erro;            // Ex: "Bad Request", "Not Found"
    private String mensagem;        // A mensagem amigável
    private String caminho;         // A URL que gerou o erro
    private List<String> detalhes;  // Para erros de validação (campos obrigatórios)
}