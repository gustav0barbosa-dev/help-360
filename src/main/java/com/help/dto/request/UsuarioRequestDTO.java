package com.help.dto.request;

import com.help.model.enums.PerfilUsuario;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    // Na criação é obrigatório, na atualização pode ser nulo (não altera)
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") // RN0021
    private String senha;

    @NotNull(message = "Perfil é obrigatório")
    private PerfilUsuario perfil;

    @NotNull(message = "Empresa é obrigatória")
    private Long empresaId;
}