package com.help.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.request.EmpresaRequestDTO;
import com.help.dto.response.EmpresaResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Empresa;
import com.help.repository.EmpresaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public EmpresaResponseDTO criar(EmpresaRequestDTO dto) {
        if (empresaRepository.existsByNome(dto.getNome())) {
            throw new RegraDeNegocioException("Já existe uma empresa com este nome.");
        }
        Empresa empresa = new Empresa();
        empresa.setNome(dto.getNome());
        empresa.setAtivo(true);
        return converterParaDTO(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO dto) {
        Empresa empresa = buscarEntidade(id);
        if (!empresa.getNome().equals(dto.getNome()) && empresaRepository.existsByNome(dto.getNome())) {
            throw new RegraDeNegocioException("Já existe uma empresa com este nome.");
        }
        empresa.setNome(dto.getNome());
        return converterParaDTO(empresaRepository.save(empresa));
    }

    @Transactional
    public void inativar(Long id, String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraDeNegocioException("Justificativa é obrigatória para inativar.");
        }
        Empresa empresa = buscarEntidade(id);
        empresa.setAtivo(false);
        empresaRepository.save(empresa);
    }

    @Transactional
    public void ativar(Long id, String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraDeNegocioException("Justificativa é obrigatória para ativar.");
        }
        Empresa empresa = buscarEntidade(id);
        empresa.setAtivo(true);
        empresaRepository.save(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarTodas() {
        return empresaRepository.findAll().stream().map(this::converterParaDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarAtivas() {
        return empresaRepository.findByAtivoTrue().stream().map(this::converterParaDTO).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarPorId(Long id) {
        return converterParaDTO(buscarEntidade(id));
    }

    private Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
    }

    private EmpresaResponseDTO converterParaDTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(empresa.getId());
        dto.setNome(empresa.getNome());
        dto.setAtivo(empresa.getAtivo());
        return dto;
    }
}