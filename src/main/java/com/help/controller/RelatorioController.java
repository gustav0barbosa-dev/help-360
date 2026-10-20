package com.help.controller;

import com.help.dto.response.RelatorioResponseDTO;
import com.help.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/tickets-por-periodo")
    public ResponseEntity<RelatorioResponseDTO> relatorioPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioPorPeriodo(inicio, fim));
    }

    @GetMapping("/desempenho")
    public ResponseEntity<RelatorioResponseDTO> relatorioDesempenho() {
        return ResponseEntity.ok(relatorioService.gerarRelatorioDesempenho());
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        byte[] excel = relatorioService.exportarExcel(inicio, fim);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}