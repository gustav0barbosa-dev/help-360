package com.help.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.request.CategoriaRequestDTO;
import com.help.dto.response.CategoriaResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Sistema;
import com.help.model.Categoria;
import com.help.repository.SistemaRepository;
import com.help.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final SistemaRepository sistemaRepository;

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Sistema sistema = sistemaRepository.findById(dto.getSistemaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sistema não encontrado."));
        if (categoriaRepository.existsByNomeAndSistemaId(dto.getNome(), dto.getSistemaId())) {
            throw new RegraDeNegocioException("Já existe uma categoria com este nome neste sistema.");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setSistema(sistema);
        categoria.setAtivo(true);
        return converterParaDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void inativar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarPorSistema(Long sistemaId) {
        return categoriaRepository.findBySistemaIdAndAtivoTrue(sistemaId)
                .stream().map(this::converterParaDTO).toList();
    }

    private CategoriaResponseDTO converterParaDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setAtivo(categoria.getAtivo());
        dto.setSistemaId(categoria.getSistema().getId());
        dto.setNomeSistema(categoria.getSistema().getNome());
        return dto;
    }
}
