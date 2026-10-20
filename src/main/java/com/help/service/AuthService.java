package com.help.service;

import com.help.dto.request.LoginRequestDTO;
import com.help.dto.response.LoginResponseDTO;
import com.help.dto.response.UsuarioResponseDTO;
import com.help.model.Usuario;
import com.help.repository.UsuarioRepository;
import com.help.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Busca o usuário direto no repositório
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Valida a senha manualmente com o BCrypt
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }

        // Gera o token JWT
        String token = tokenService.gerarToken(usuario);

        return new LoginResponseDTO(token, "Bearer", converterParaDTO(usuario));
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        dto.setAtivo(usuario.getAtivo());
        dto.setNomeEmpresa(usuario.getEmpresa() != null ? usuario.getEmpresa().getNome() : null);
        return dto;
    }
}