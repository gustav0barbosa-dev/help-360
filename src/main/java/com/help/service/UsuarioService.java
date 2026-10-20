package com.help.service;

import com.help.dto.request.UsuarioRequestDTO;
import com.help.dto.response.UsuarioResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Empresa;
import com.help.model.Usuario;
import com.help.repository.EmpresaRepository;
import com.help.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.help.model.enums.PerfilUsuario;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraDeNegocioException("Já existe um usuário com este e-mail.");
        }
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        return converterParaDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);
        usuario.setNome(dto.getNome());
        usuario.setPerfil(dto.getPerfil());
        // A senha só é atualizada se for informada
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        return converterParaDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void ativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarAtendentes() {
        return usuarioRepository.findByPerfilAndAtivoTrue(PerfilUsuario.ATENDENTE)
                .stream().map(this::converterParaDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream().map(this::converterParaDTO).toList();
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
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