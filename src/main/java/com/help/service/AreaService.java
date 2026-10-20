package com.help.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.request.AreaRequestDTO;
import com.help.dto.response.AreaResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Area;
import com.help.model.Empresa;
import com.help.repository.AreaRepository;
import com.help.repository.EmpresaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public AreaResponseDTO criar(AreaRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        if (areaRepository.existsByNomeAndEmpresaId(dto.getNome(), dto.getEmpresaId())) {
            throw new RegraDeNegocioException("Já existe uma área com este nome nesta empresa.");
        }
        Area area = new Area();
        area.setNome(dto.getNome());
        area.setEmpresa(empresa);
        area.setAtivo(true);
        return converterParaDTO(areaRepository.save(area));
    }

    @Transactional
    public AreaResponseDTO atualizar(Long id, AreaRequestDTO dto) {
        Area area = buscarEntidade(id);
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        area.setNome(dto.getNome());
        area.setEmpresa(empresa);
        return converterParaDTO(areaRepository.save(area));
    }

    @Transactional
    public void inativar(Long id) {
        Area area = buscarEntidade(id);
        area.setAtivo(false);
        areaRepository.save(area);
    }

    @Transactional
    public void ativar(Long id) {
        Area area = buscarEntidade(id);
        area.setAtivo(true);
        areaRepository.save(area);
    }

    @Transactional(readOnly = true)
    public List<AreaResponseDTO> listarPorEmpresa(Long empresaId) {
        return areaRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream().map(this::converterParaDTO).toList();
    }

    @Transactional(readOnly = true)
    public AreaResponseDTO buscarPorId(Long id) {
        return converterParaDTO(buscarEntidade(id));
    }

    private Area buscarEntidade(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Área não encontrada."));
    }

    private AreaResponseDTO converterParaDTO(Area area) {
        AreaResponseDTO dto = new AreaResponseDTO();
        dto.setId(area.getId());
        dto.setNome(area.getNome());
        dto.setAtivo(area.getAtivo());
        dto.setEmpresaId(area.getEmpresa().getId());
        dto.setNomeEmpresa(area.getEmpresa().getNome());
        return dto;
    }
}