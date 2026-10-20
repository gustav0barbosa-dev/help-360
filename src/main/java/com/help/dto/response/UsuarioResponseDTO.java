package com.help.dto.response;

import com.help.model.enums.PerfilUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private Boolean ativo;
    private String nomeEmpresa;
}