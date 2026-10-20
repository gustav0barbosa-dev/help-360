package com.help.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.help.dto.request.CategoriaRequestDTO;
import com.help.dto.response.CategoriaResponseDTO;
import com.help.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        categoriaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sistema/{sistemaId}")
    public ResponseEntity<List<CategoriaResponseDTO>> listarPorSistema(@PathVariable Long sistemaId) {
        return ResponseEntity.ok(categoriaService.listarPorSistema(sistemaId));
    }
}