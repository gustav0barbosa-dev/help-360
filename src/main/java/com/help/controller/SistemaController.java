package com.help.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.help.dto.request.SistemaRequestDTO;
import com.help.dto.response.SistemaResponseDTO;
import com.help.service.SistemaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sistemas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SistemaController {
    private final SistemaService sistemaService;

    @PostMapping
    public ResponseEntity<SistemaResponseDTO> criar(@RequestBody @Valid SistemaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sistemaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SistemaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid SistemaRequestDTO dto) {
        return ResponseEntity.ok(sistemaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        sistemaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        sistemaService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SistemaResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(sistemaService.listarAtivos());
    }
}