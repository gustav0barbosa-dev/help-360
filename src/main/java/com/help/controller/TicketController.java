package com.help.controller;

import com.help.dto.filter.FiltroTicketDTO;
import com.help.dto.request.JustificativaRequestDTO;
import com.help.dto.request.TicketRequestDTO;
import com.help.dto.response.TicketResponseDTO;
import com.help.dto.response.TicketResumoDTO;
import com.help.model.Usuario;
import com.help.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> criar(
            @RequestBody @Valid TicketRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        
        TicketResponseDTO novoTicket = ticketService.criar(dto, usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTicket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResumoDTO>> listar(FiltroTicketDTO filtro) {
        return ResponseEntity.ok(ticketService.listar(filtro));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<TicketResumoDTO>> listarAtivos() {
        return ResponseEntity.ok(ticketService.listarAtivos());
    }

    @GetMapping("/meus")
    public ResponseEntity<List<TicketResumoDTO>> listarMeusTickets(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(ticketService.listarMeusTickets(usuarioLogado.getId()));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<TicketResponseDTO> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.fechar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TicketResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestBody @Valid JustificativaRequestDTO dto) {
        return ResponseEntity.ok(ticketService.cancelar(id, dto.getJustificativa()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        ticketService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}