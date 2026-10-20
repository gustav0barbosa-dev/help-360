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

import com.help.dto.request.AreaRequestDTO;
import com.help.dto.response.AreaResponseDTO;
import com.help.service.AreaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    public ResponseEntity<AreaResponseDTO> criar(@RequestBody @Valid AreaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AreaRequestDTO dto) {
        return ResponseEntity.ok(areaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        areaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        areaService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<AreaResponseDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(areaService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }
}