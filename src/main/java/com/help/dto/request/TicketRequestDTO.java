package com.help.dto.request;

import com.help.model.enums.Prioridade;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TicketRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200)
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Prioridade é obrigatória")
    private Prioridade prioridade;

    @NotNull(message = "Empresa é obrigatória")
    private Long empresaId;

    @NotNull(message = "Área é obrigatória")
    private Long areaId;

    @NotNull(message = "Sistema é obrigatório")
    private Long sistemaId;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    // Opcionais
    private Long responsavelId;              // RN0064 - validado no Service
    private List<Long> ticketsRelacionadosIds;
    private String mensagemInicial;          // RN0031
    private Boolean publico = true;          // RN0031
    // O anexo é tratado via MultipartFile no Controller
}