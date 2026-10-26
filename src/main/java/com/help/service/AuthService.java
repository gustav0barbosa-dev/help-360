package com.help.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.request.AlterarSenhaRequestDTO;
import com.help.dto.request.LoginRequestDTO;
import com.help.dto.response.LoginResponseDTO;
import com.help.dto.response.UsuarioResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Usuario;
import com.help.repository.UsuarioRepository;
import com.help.security.TokenService;

import lombok.RequiredArgsConstructor;

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

    @Transactional
    public void alterarSenha(AlterarSenhaRequestDTO dto, Long usuarioId) {
        // 1. Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        // 2. Verifica se a senha atual está correta
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new RegraDeNegocioException("Senha atual incorreta.");
        }

        // 3. Verifica se a nova senha é diferente da atual
        if (passwordEncoder.matches(dto.getNovaSenha(), usuario.getSenha())) {
            throw new RegraDeNegocioException("A nova senha deve ser diferente da senha atual.");
        }

        // 4. Atualiza a senha criptografada
        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
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