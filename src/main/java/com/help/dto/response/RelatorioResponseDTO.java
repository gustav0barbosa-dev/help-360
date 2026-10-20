package com.help.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class RelatorioResponseDTO {

    private List<ItemRelatorioDTO> itens;
    private BigDecimal tempoMedioGeral;

    @Getter @Setter
    public static class ItemRelatorioDTO {
        private String categoria;
        private String responsavel;
        private Long totalTickets;
        private Long ticketsResolvidos;
        private Long tempoMedioMinutos;
    }
}