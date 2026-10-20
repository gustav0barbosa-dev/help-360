package com.help.service;

import com.help.dto.response.RelatorioResponseDTO;
import com.help.exception.RegraDeNegocioException;
import com.help.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final TicketRepository ticketRepository;

    public RelatorioResponseDTO gerarRelatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
        // RN0071 - Mínimo 1 mês, máximo 24 meses
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(inicio, fim);
        if (meses < 1 || meses > 24) {
            throw new RegraDeNegocioException("O período deve ser entre 1 e 24 meses.");
        }

        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(23, 59, 59);

        List<Object[]> resultados = ticketRepository.gerarRelatorioPorPeriodo(inicioDt, fimDt);
        
        RelatorioResponseDTO response = new RelatorioResponseDTO();
        List<RelatorioResponseDTO.ItemRelatorioDTO> itens = resultados.stream().map(r -> {
            RelatorioResponseDTO.ItemRelatorioDTO item = new RelatorioResponseDTO.ItemRelatorioDTO();
            item.setCategoria((String) r[0]);
            item.setTotalTickets((Long) r[1]);
            item.setTicketsResolvidos((Long) r[2]);
            return item;
        }).toList();
        
        response.setItens(itens);
        return response;
    }

    public RelatorioResponseDTO gerarRelatorioDesempenho() {
        List<Object[]> resultados = ticketRepository.calcularTempoMedioPorResponsavel();
        
        RelatorioResponseDTO response = new RelatorioResponseDTO();
        List<RelatorioResponseDTO.ItemRelatorioDTO> itens = resultados.stream().map(r -> {
            RelatorioResponseDTO.ItemRelatorioDTO item = new RelatorioResponseDTO.ItemRelatorioDTO();
            item.setResponsavel((String) r[0]);
            Double tempoMedio = (Double) r[1];
            item.setTempoMedioMinutos(tempoMedio != null ? tempoMedio.longValue() : 0L);
            return item;
        }).toList();
        
        response.setItens(itens);
        return response;
    }

    // Exportar para Excel (RF0073)
    public byte[] exportarExcel(LocalDate inicio, LocalDate fim) {
        RelatorioResponseDTO relatorio = gerarRelatorioPorPeriodo(inicio, fim);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório de Tickets");
            
            // Cabeçalho
            Row header = sheet.createRow(0);
            String[] colunas = {"Categoria", "Total de Tickets", "Tickets Resolvidos"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(colunas[i]);
                
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }
            
            // Dados
            int rowNum = 1;
            for (RelatorioResponseDTO.ItemRelatorioDTO item : relatorio.getItens()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getCategoria());
                row.createCell(1).setCellValue(item.getTotalTickets());
                row.createCell(2).setCellValue(item.getTicketsResolvidos());
            }
            
            // Auto-ajustar colunas
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel.", e);
        }
    }
}