package com.help.specification;

import com.help.dto.filter.FiltroTicketDTO;
import com.help.model.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TicketSpecification {

    public static Specification<Ticket> comFiltros(FiltroTicketDTO filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getId() != null) 
                predicates.add(cb.equal(root.get("id"), filtro.getId()));
            if (filtro.getTitulo() != null) 
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + filtro.getTitulo().toLowerCase() + "%"));
            if (filtro.getPrioridade() != null) 
                predicates.add(cb.equal(root.get("prioridade"), filtro.getPrioridade()));
            if (filtro.getStatus() != null) 
                predicates.add(cb.equal(root.get("status"), filtro.getStatus()));
            if (filtro.getClassificacao() != null) 
                predicates.add(cb.equal(root.get("classificacao"), filtro.getClassificacao()));
            if (filtro.getEmpresaId() != null) 
                predicates.add(cb.equal(root.get("empresa").get("id"), filtro.getEmpresaId()));
            if (filtro.getSistemaId() != null) 
                predicates.add(cb.equal(root.get("sistema").get("id"), filtro.getSistemaId()));
            if (filtro.getCategoriaId() != null) 
                predicates.add(cb.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            if (filtro.getResponsavelId() != null) 
                predicates.add(cb.equal(root.get("responsavel").get("id"), filtro.getResponsavelId()));
            if (filtro.getSolicitanteId() != null) 
                predicates.add(cb.equal(root.get("solicitante").get("id"), filtro.getSolicitanteId()));
            
            if (filtro.getDataCriacaoInicio() != null) 
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoInicio().atStartOfDay()));
            if (filtro.getDataCriacaoFim() != null) 
                predicates.add(cb.lessThanOrEqualTo(root.get("dataCriacao"), filtro.getDataCriacaoFim().atTime(23, 59, 59)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}