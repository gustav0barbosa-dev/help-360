package com.help.service;

import com.help.dto.request.ApontamentoRequestDTO;
import com.help.dto.response.ApontamentoResponseDTO;
import com.help.exception.AcessoNegadoException;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.ApontamentoDeTrabalho;
import com.help.model.Ticket;
import com.help.model.Usuario;
import com.help.model.enums.PerfilUsuario;
import com.help.model.enums.StatusTicket;
import com.help.repository.ApontamentoDeTrabalhoRepository;
import com.help.repository.TicketRepository;
import com.help.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApontamentoService {

    private final ApontamentoDeTrabalhoRepository apontamentoRepository;
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ApontamentoResponseDTO iniciar(ApontamentoRequestDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado."));

        if (usuario.getPerfil() == PerfilUsuario.SOLICITANTE) {
            throw new AcessoNegadoException("Apenas atendentes podem iniciar apontamentos.");
        }

        if (ticket.getStatus() == StatusTicket.FECHADO || ticket.getStatus() == StatusTicket.CANCELADO) {
            throw new RegraDeNegocioException("Não é possível iniciar trabalho em ticket fechado/cancelado.");
        }

        if (apontamentoRepository.findByUsuarioIdAndDataFimIsNull(usuarioId).isPresent()) {
            throw new RegraDeNegocioException("Você já possui um trabalho em andamento.");
        }

        ApontamentoDeTrabalho apontamento = new ApontamentoDeTrabalho();
        apontamento.setTicket(ticket);
        apontamento.setUsuario(usuario);
        apontamento.setTipoTrabalho(dto.getTipoTrabalho());
        apontamento.setDescricao(dto.getDescricao());
        apontamento.setDataInicio(LocalDateTime.now());

        return converterParaDTO(apontamentoRepository.save(apontamento));
    }

    @Transactional
    public ApontamentoResponseDTO finalizar(Long apontamentoId) {
        ApontamentoDeTrabalho apontamento = apontamentoRepository.findById(apontamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Apontamento não encontrado."));

        if (apontamento.getDataFim() != null) {
            throw new RegraDeNegocioException("Este apontamento já foi finalizado.");
        }

        apontamento.setDataFim(LocalDateTime.now());
        Duration duracao = Duration.between(apontamento.getDataInicio(), apontamento.getDataFim());
        apontamento.setTempoGastoMinutos(duracao.toMinutes());

        return converterParaDTO(apontamentoRepository.save(apontamento));
    }

    private ApontamentoResponseDTO converterParaDTO(ApontamentoDeTrabalho apontamento) {
        ApontamentoResponseDTO dto = new ApontamentoResponseDTO();
        dto.setId(apontamento.getId());
        dto.setDataInicio(apontamento.getDataInicio());
        dto.setDataFim(apontamento.getDataFim());
        dto.setTempoGastoMinutos(apontamento.getTempoGastoMinutos());
        dto.setTipoTrabalho(apontamento.getTipoTrabalho());
        dto.setDescricao(apontamento.getDescricao());
        dto.setNomeUsuario(apontamento.getUsuario().getNome());
        dto.setNomeTicket(apontamento.getTicket().getTitulo());
        return dto;
    }
}