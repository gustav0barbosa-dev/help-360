package com.help.service;

import com.help.dto.request.TicketRequestDTO;
import com.help.dto.response.TicketResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.*;
import com.help.model.enums.Prioridade;
import com.help.model.enums.StatusTicket;
import com.help.repository.*;
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
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private AreaRepository areaRepository;
    @Mock private SistemaRepository sistemaRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private MensagemService mensagemService;

    @InjectMocks
    private TicketService ticketService;

    private Usuario solicitante;
    private Empresa empresa;
    private Area area;
    private Sistema sistema;
    private Categoria categoria;
    private TicketRequestDTO dto;

    @BeforeEach
    void setUp() {
        solicitante = new Usuario();
        solicitante.setId(1L);
        solicitante.setNome("Solicitante Teste");

        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("SSPREV");

        area = new Area();
        area.setId(1L);
        area.setNome("TI");
        area.setEmpresa(empresa);

        sistema = new Sistema();
        sistema.setId(1L);
        sistema.setNome("SIGEPREV");

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Cadastro");
        categoria.setSistema(sistema);

        dto = new TicketRequestDTO();
        dto.setTitulo("Teste de Ticket");
        dto.setDescricao("Descrição do teste");
        dto.setPrioridade(Prioridade.URGENTE);
        dto.setEmpresaId(1L);
        dto.setAreaId(1L);
        dto.setSistemaId(1L);
        dto.setCategoriaId(1L);
    }

    @Test
    @DisplayName("Deve criar um ticket com status ABERTO e previsão de SLA correta (Urgente = 3h)")
    void deveCriarTicketComSucesso() {
        // Arrange (preparação)
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(sistemaRepository.findById(1L)).thenReturn(Optional.of(sistema));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Ticket ticketSalvo = new Ticket();
        ticketSalvo.setId(1L);
        ticketSalvo.setTitulo(dto.getTitulo());
        ticketSalvo.setDescricao(dto.getDescricao());
        ticketSalvo.setPrioridade(dto.getPrioridade());
        ticketSalvo.setStatus(StatusTicket.ABERTO);
        ticketSalvo.setDataCriacao(LocalDateTime.now());
        ticketSalvo.setPrevisaoSolucao(LocalDateTime.now().plusHours(3));
        ticketSalvo.setSolicitante(solicitante);
        ticketSalvo.setEmpresa(empresa);
        ticketSalvo.setArea(area);
        ticketSalvo.setSistema(sistema);
        ticketSalvo.setCategoria(categoria);

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketSalvo);

        // Act (ação)
        TicketResponseDTO resultado = ticketService.criar(dto, 1L);

        // Assert (verificação)
        assertNotNull(resultado);
        assertEquals(StatusTicket.ABERTO, resultado.getStatus());
        assertEquals(Prioridade.URGENTE, resultado.getPrioridade());
        assertNotNull(resultado.getPrevisaoSolucao());

        // Verifica se a mensagem inicial não foi enviada (pois o DTO não tem mensagem)
        verify(mensagemService, never()).adicionarMensagem(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o solicitante não existe")
    void deveLancarExcecaoQuandoSolicitanteNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> ticketService.criar(dto, 1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar fechar um ticket já FECHADO")
    void deveLancarExcecaoAoFecharTicketJaFechado() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(StatusTicket.FECHADO);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(RegraDeNegocioException.class,
                () -> ticketService.fechar(1L));
    }

    @Test
    @DisplayName("Deve calcular SLA de 3 horas para prioridade URGENTE")
    void deveCalcularSlaUrgente() {
        LocalDateTime dataCriacao = LocalDateTime.now();
        // Usando reflection para chamar o método privado
        try {
            var metodo = TicketService.class.getDeclaredMethod("calcularPrevisao", Prioridade.class, LocalDateTime.class);
            metodo.setAccessible(true);
            LocalDateTime previsao = (LocalDateTime) metodo.invoke(ticketService, Prioridade.URGENTE, dataCriacao);
            
            assertEquals(dataCriacao.plusHours(3), previsao);
        } catch (Exception e) {
            fail("Erro ao acessar método privado: " + e.getMessage());
        }
    }
}