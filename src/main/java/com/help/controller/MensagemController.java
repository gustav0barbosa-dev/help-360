package com.help.controller;

import com.help.dto.request.MensagemRequestDTO;
import com.help.dto.response.MensagemResponseDTO;
import com.help.model.Usuario;
import com.help.service.MensagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping
    public ResponseEntity<MensagemResponseDTO> adicionar(
            @PathVariable Long ticketId,
            @RequestBody @Valid MensagemRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        
        MensagemResponseDTO mensagem = mensagemService.adicionarMensagem(
                ticketId, dto.getTexto(), dto.getPublico(), usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
    }

    @GetMapping
    public ResponseEntity<List<MensagemResponseDTO>> listar(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        // A validação se é Atendente/Admin para ver mensagens internas será tratada no Service ou via @PreAuthorize
        List<MensagemResponseDTO> mensagens = mensagemService.listarPorTicket(ticketId, true);
        return ResponseEntity.ok(mensagens);
    }
}