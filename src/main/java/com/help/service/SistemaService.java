package com.help.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.request.SistemaRequestDTO;
import com.help.dto.response.SistemaResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Sistema;
import com.help.repository.SistemaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SistemaService {
    private final SistemaRepository sistemaRepository;

    @Transactional
    public SistemaResponseDTO criar(SistemaRequestDTO dto) {
        if (sistemaRepository.existsByNome(dto.getNome())) {
            throw new RegraDeNegocioException("Já existe um sistema com este nome.");
        }
        Sistema sistema = new Sistema();
        sistema.setNome(dto.getNome());
        sistema.setAtivo(true);
        return converterParaDTO(sistemaRepository.save(sistema));
    }

    @Transactional
    public SistemaResponseDTO atualizar(Long id, SistemaRequestDTO dto) {
        Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sistema não encontrado."));
        sistema.setNome(dto.getNome());
        return converterParaDTO(sistemaRepository.save(sistema));
    }

    @Transactional
    public void inativar(Long id) {
        Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sistema não encontrado."));
        sistema.setAtivo(false);
        sistemaRepository.save(sistema);
    }

    @Transactional
    public void ativar(Long id) {
        Sistema sistema = sistemaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sistema não encontrado."));
        sistema.setAtivo(true);
        sistemaRepository.save(sistema);
    }

    @Transactional(readOnly = true)
    public List<SistemaResponseDTO> listarAtivos() {
        return sistemaRepository.findByAtivoTrue().stream().map(this::converterParaDTO).toList();
    }

    private SistemaResponseDTO converterParaDTO(Sistema sistema) {
        SistemaResponseDTO dto = new SistemaResponseDTO();
        dto.setId(sistema.getId());
        dto.setNome(sistema.getNome());
        dto.setAtivo(sistema.getAtivo());
        return dto;
    }
}