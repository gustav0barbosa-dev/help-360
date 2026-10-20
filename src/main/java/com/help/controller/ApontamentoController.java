package com.help.controller;

import com.help.dto.request.ApontamentoRequestDTO;
import com.help.dto.response.ApontamentoResponseDTO;
import com.help.model.Usuario;
import com.help.service.ApontamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/apontamentos")
@RequiredArgsConstructor
public class ApontamentoController {

    private final ApontamentoService apontamentoService;

    @PostMapping
    public ResponseEntity<ApontamentoResponseDTO> iniciar(
            @RequestBody @Valid ApontamentoRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        
        ApontamentoResponseDTO apontamento = apontamentoService.iniciar(dto, usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(apontamento);
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ApontamentoResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(apontamentoService.finalizar(id));
    }
}