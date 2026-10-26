package com.help.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.help.dto.filter.FiltroTicketDTO;
import com.help.dto.request.TicketRequestDTO;
import com.help.dto.response.TicketResponseDTO;
import com.help.dto.response.TicketResumoDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Area;
import com.help.model.Categoria;
import com.help.model.Empresa;
import com.help.model.Sistema;
import com.help.model.Ticket;
import com.help.model.Usuario;
import com.help.model.enums.Prioridade;
import com.help.model.enums.StatusTicket;
import com.help.repository.AreaRepository;
import com.help.repository.CategoriaRepository;
import com.help.repository.EmpresaRepository;
import com.help.repository.SistemaRepository;
import com.help.repository.TicketRepository;
import com.help.repository.UsuarioRepository;
import com.help.specification.TicketSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final AreaRepository areaRepository;
    private final SistemaRepository sistemaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MensagemService mensagemService;

    @Transactional
    public TicketResponseDTO criar(TicketRequestDTO dto, Long solicitanteId) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitante não encontrado."));

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Área não encontrada."));
        Sistema sistema = sistemaRepository.findById(dto.getSistemaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sistema não encontrado."));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        Ticket ticket = new Ticket();
        ticket.setTitulo(dto.getTitulo());
        ticket.setDescricao(dto.getDescricao());
        ticket.setPrioridade(dto.getPrioridade());
        ticket.setEmpresa(empresa);
        ticket.setArea(area);
        ticket.setSistema(sistema);
        ticket.setCategoria(categoria);
        ticket.setSolicitante(solicitante);
        ticket.setStatus(StatusTicket.ABERTO);
        ticket.setDataCriacao(LocalDateTime.now());
        ticket.setPrevisaoSolucao(calcularPrevisao(dto.getPrioridade(), ticket.getDataCriacao()));

        if (dto.getResponsavelId() != null) {
            Usuario responsavel = usuarioRepository.findById(dto.getResponsavelId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado."));
            ticket.setResponsavel(responsavel);
        }

        if (dto.getTicketsRelacionadosIds() != null && !dto.getTicketsRelacionadosIds().isEmpty()) {
            List<Ticket> relacionados = ticketRepository.findAllById(dto.getTicketsRelacionadosIds());
            ticket.setTicketsRelacionados(relacionados);
        }

        Ticket ticketSalvo = ticketRepository.save(ticket);

        if (dto.getMensagemInicial() != null && !dto.getMensagemInicial().isBlank()) {
            mensagemService.adicionarMensagem(ticketSalvo.getId(), dto.getMensagemInicial(), dto.getPublico(), solicitanteId);
        }

        return converterParaDTO(ticketSalvo);
    }

    private LocalDateTime calcularPrevisao(Prioridade prioridade, LocalDateTime dataCriacao) {
        return switch (prioridade) {
            case URGENTE -> dataCriacao.plusHours(3);
            case MEDIA_2 -> dataCriacao.plusDays(9);
            case BAIXA_2 -> dataCriacao.plusDays(27);
        };
    }

    @Transactional(readOnly = true)
    public Page<TicketResumoDTO> listar(FiltroTicketDTO filtro) {
        PageRequest pageRequest = PageRequest.of(
                filtro.getPagina(), 
                filtro.getTamanho(), 
                Sort.Direction.valueOf(filtro.getDirecao()), 
                filtro.getOrdenacao()
        );
        return ticketRepository.findAll(TicketSpecification.comFiltros(filtro), pageRequest)
                .map(this::converterParaResumoDTO);
    }

    public List<TicketResumoDTO> listarMeusTickets(Long usuarioId) {
        return ticketRepository.findMeusTicketsComRelacionamentos(usuarioId) 
                .stream().map(this::converterParaResumoDTO).toList();
    }

    public List<TicketResumoDTO> listarAtivos() {
        return ticketRepository.findByStatusNotInComRelacionamentos(List.of(StatusTicket.FECHADO, StatusTicket.RESOLVIDO)) 
                .stream().map(this::converterParaResumoDTO).toList();
    }

    @Transactional
    public TicketResponseDTO fechar(Long ticketId) {
        Ticket ticket = buscarEntidade(ticketId);
        
        if (ticket.getStatus() == StatusTicket.FECHADO || ticket.getStatus() == StatusTicket.CANCELADO) {
            throw new RegraDeNegocioException("Ticket não pode ser fechado no status atual.");
        }

        ticket.setStatus(StatusTicket.FECHADO);
        ticket.setDataSolucao(LocalDateTime.now());

        return converterParaDTO(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponseDTO cancelar(Long id, String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraDeNegocioException("Justificativa é obrigatória para cancelar o ticket.");
        }
        
        Ticket ticket = buscarEntidade(id);
        
        if (ticket.getStatus() == StatusTicket.FECHADO || ticket.getStatus() == StatusTicket.CANCELADO) {
            throw new RegraDeNegocioException("Ticket não pode ser cancelado no status atual.");
        }

        ticket.setStatus(StatusTicket.CANCELADO);
        return converterParaDTO(ticketRepository.save(ticket));
    }

    @Transactional
    public void excluir(Long id) {
        Ticket ticket = buscarEntidade(id);
        
        if (ticket.getStatus() != StatusTicket.FECHADO) {
            throw new RegraDeNegocioException("Apenas tickets com status FECHADO podem ser excluídos.");
        }
        
        ticketRepository.delete(ticket);
    }
    
    public TicketResponseDTO buscarPorId(Long id) {
        Ticket ticket = ticketRepository.findByIdComRelacionamentos(id) 
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado."));
        return converterParaDTO(ticket);
    }

    private Ticket buscarEntidade(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado."));
    }

    private TicketResponseDTO converterParaDTO(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setTitulo(ticket.getTitulo());
        dto.setDescricao(ticket.getDescricao());
        dto.setPrioridade(ticket.getPrioridade());
        dto.setStatus(ticket.getStatus());
        dto.setClassificacao(ticket.getClassificacao());
        dto.setDataCriacao(ticket.getDataCriacao());
        dto.setPrevisaoSolucao(ticket.getPrevisaoSolucao());
        dto.setDataSolucao(ticket.getDataSolucao());
        dto.setNomeSolicitante(ticket.getSolicitante().getNome());
        dto.setNomeResponsavel(ticket.getResponsavel() != null ? ticket.getResponsavel().getNome() : null);
        dto.setNomeEmpresa(ticket.getEmpresa().getNome());
        dto.setNomeArea(ticket.getArea().getNome());
        dto.setNomeSistema(ticket.getSistema().getNome());
        dto.setNomeCategoria(ticket.getCategoria().getNome());
        return dto;
    }

    private TicketResumoDTO converterParaResumoDTO(Ticket ticket) {
        TicketResumoDTO dto = new TicketResumoDTO();
        dto.setId(ticket.getId());
        dto.setPrioridade(ticket.getPrioridade());
        dto.setTitulo(ticket.getTitulo());
        dto.setDataCriacao(ticket.getDataCriacao());
        dto.setNomeSistema(ticket.getSistema().getNome());
        dto.setStatus(ticket.getStatus());
        dto.setNomeCategoria(ticket.getCategoria().getNome());
        dto.setNomeEmpresa(ticket.getEmpresa().getNome());
        dto.setNomeResponsavel(ticket.getResponsavel() != null ? ticket.getResponsavel().getNome() : null);
        dto.setPrevisaoSolucao(ticket.getPrevisaoSolucao());
        dto.setDataSolucao(ticket.getDataSolucao());
        dto.setClassificacao(ticket.getClassificacao());
        return dto;
    }
}