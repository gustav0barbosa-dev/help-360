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

import com.help.dto.request.EmpresaRequestDTO;
import com.help.dto.request.JustificativaRequestDTO;
import com.help.dto.response.EmpresaResponseDTO;
import com.help.service.EmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criar(@RequestBody @Valid EmpresaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid EmpresaRequestDTO dto) {
        return ResponseEntity.ok(empresaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id, @RequestBody @Valid JustificativaRequestDTO dto) {
        empresaService.inativar(id, dto.getJustificativa());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id, @RequestBody @Valid JustificativaRequestDTO dto) {
        empresaService.ativar(id, dto.getJustificativa());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<EmpresaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(empresaService.listarAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.buscarPorId(id));
    }
}