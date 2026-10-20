package com.help.controller;

import com.help.dto.response.AnexoResponseDTO;
import com.help.model.Usuario;
import com.help.service.AnexoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnexoController {

    private final AnexoService anexoService;

    // Upload de arquivo (vinculado a um ticket)
    @PostMapping("/tickets/{ticketId}/anexos")
    public ResponseEntity<AnexoResponseDTO> upload(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        AnexoResponseDTO dto = anexoService.upload(ticketId, file, usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Listar anexos de um ticket
    @GetMapping("/tickets/{ticketId}/anexos")
    public ResponseEntity<List<AnexoResponseDTO>> listar(@PathVariable Long ticketId) {
        return ResponseEntity.ok(anexoService.listarPorTicket(ticketId));
    }

    // Download do arquivo
    @GetMapping("/anexos/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = anexoService.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Excluir anexo
    @DeleteMapping("/anexos/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        anexoService.excluir(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }
}