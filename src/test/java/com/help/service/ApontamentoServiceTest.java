package com.help.service;

import com.help.dto.request.ApontamentoRequestDTO;
import com.help.dto.response.ApontamentoResponseDTO;
import com.help.exception.AcessoNegadoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.ApontamentoDeTrabalho;
import com.help.model.Ticket;
import com.help.model.Usuario;
import com.help.model.enums.PerfilUsuario;
import com.help.model.enums.StatusTicket;
import com.help.model.enums.TipoTrabalho;
import com.help.repository.ApontamentoDeTrabalhoRepository;
import com.help.repository.TicketRepository;
import com.help.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApontamentoServiceTest {

    @Mock private ApontamentoDeTrabalhoRepository apontamentoRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ApontamentoService apontamentoService;

    private Usuario atendente;
    private Ticket ticketAberto;

    @BeforeEach
    void setUp() {
        atendente = new Usuario();
        atendente.setId(2L);
        atendente.setNome("Atendente");
        atendente.setPerfil(PerfilUsuario.ATENDENTE);

        ticketAberto = new Ticket();
        ticketAberto.setId(1L);
        ticketAberto.setTitulo("Ticket Teste");
        ticketAberto.setStatus(StatusTicket.ABERTO);
    }

    @Test
    @DisplayName("Deve iniciar apontamento com sucesso para ATENDENTE em ticket ABERTO")
    void deveIniciarApontamentoComSucesso() {
        ApontamentoRequestDTO dto = new ApontamentoRequestDTO();
        dto.setTicketId(1L);
        dto.setTipoTrabalho(TipoTrabalho.TICKET);
        dto.setDescricao("Iniciando");

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(atendente));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketAberto));
        when(apontamentoRepository.findByUsuarioIdAndDataFimIsNull(2L)).thenReturn(Optional.empty());

        ApontamentoDeTrabalho apontamentoSalvo = new ApontamentoDeTrabalho();
        apontamentoSalvo.setId(1L);
        apontamentoSalvo.setUsuario(atendente);
        apontamentoSalvo.setTicket(ticketAberto);
        apontamentoSalvo.setDataInicio(LocalDateTime.now());
        apontamentoSalvo.setTipoTrabalho(TipoTrabalho.TICKET);

        when(apontamentoRepository.save(any(ApontamentoDeTrabalho.class))).thenReturn(apontamentoSalvo);

        ApontamentoResponseDTO resultado = apontamentoService.iniciar(dto, 2L);

        assertNotNull(resultado);
        assertEquals("Atendente", resultado.getNomeUsuario());
        assertEquals("Ticket Teste", resultado.getNomeTicket());
        assertNull(resultado.getDataFim());
    }

    @Test
    @DisplayName("Não deve permitir SOLICITANTE iniciar apontamento")
    void naoDevePermitirSolicitanteIniciarApontamento() {
        Usuario solicitante = new Usuario();
        solicitante.setId(3L);
        solicitante.setPerfil(PerfilUsuario.SOLICITANTE);

        ApontamentoRequestDTO dto = new ApontamentoRequestDTO();
        dto.setTicketId(1L);

        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(solicitante));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketAberto));

        assertThrows(AcessoNegadoException.class,
                () -> apontamentoService.iniciar(dto, 3L));
    }

    @Test
    @DisplayName("Não deve permitir iniciar apontamento em ticket FECHADO")
    void naoDevePermitirApontamentoEmTicketFechado() {
        ticketAberto.setStatus(StatusTicket.FECHADO);

        ApontamentoRequestDTO dto = new ApontamentoRequestDTO();
        dto.setTicketId(1L);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(atendente));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketAberto));

        assertThrows(RegraDeNegocioException.class,
                () -> apontamentoService.iniciar(dto, 2L));
    }

    @Test
    @DisplayName("Não deve permitir dois apontamentos abertos para o mesmo usuário")
    void naoDevePermitirDoisApontamentosAbertos() {
        ApontamentoRequestDTO dto = new ApontamentoRequestDTO();
        dto.setTicketId(1L);

        ApontamentoDeTrabalho apontamentoAberto = new ApontamentoDeTrabalho();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(atendente));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticketAberto));
        when(apontamentoRepository.findByUsuarioIdAndDataFimIsNull(2L)).thenReturn(Optional.of(apontamentoAberto));

        assertThrows(RegraDeNegocioException.class,
                () -> apontamentoService.iniciar(dto, 2L));
    }

    @Test
    @DisplayName("Deve calcular o tempo gasto corretamente ao finalizar")
    void deveCalcularTempoGastoAoFinalizar() {
        ApontamentoDeTrabalho apontamento = new ApontamentoDeTrabalho();
        apontamento.setId(1L);
        apontamento.setUsuario(atendente);
        apontamento.setTicket(ticketAberto);
        apontamento.setDataInicio(LocalDateTime.now().minusMinutes(30));
        apontamento.setTipoTrabalho(TipoTrabalho.TICKET);

        when(apontamentoRepository.findById(1L)).thenReturn(Optional.of(apontamento));
        when(apontamentoRepository.save(any(ApontamentoDeTrabalho.class))).thenAnswer(i -> i.getArgument(0));

        ApontamentoResponseDTO resultado = apontamentoService.finalizar(1L);

        assertNotNull(resultado.getDataFim());
        assertTrue(resultado.getTempoGastoMinutos() >= 30);
    }
}