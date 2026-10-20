package com.help.service;

import com.help.dto.response.MensagemResponseDTO;
import com.help.model.Mensagem;
import com.help.model.Ticket;
import com.help.model.Usuario;
import com.help.repository.MensagemRepository;
import com.help.repository.TicketRepository;
import com.help.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MensagemResponseDTO adicionarMensagem(Long ticketId, String texto, Boolean publico, Long autorId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado."));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(texto);
        mensagem.setPublico(publico != null ? publico : true); // RN0031
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagem.setTicket(ticket);
        mensagem.setAutor(autor); // RN0032

        return converterParaDTO(mensagemRepository.save(mensagem));
    }

    // RN0031 - Filtra mensagens internas se o solicitante for o visualizador
    public List<MensagemResponseDTO> listarPorTicket(Long ticketId, boolean isAtendenteOuAdmin) {
        List<Mensagem> mensagens = isAtendenteOuAdmin 
            ? mensagemRepository.findByTicketIdOrderByDataEnvioAsc(ticketId)
            : mensagemRepository.findByTicketIdAndPublicoTrueOrderByDataEnvioAsc(ticketId);
            
        return mensagens.stream().map(this::converterParaDTO).toList();
    }

    private MensagemResponseDTO converterParaDTO(Mensagem mensagem) {
        MensagemResponseDTO dto = new MensagemResponseDTO();
        dto.setId(mensagem.getId());
        dto.setTexto(mensagem.getTexto());
        dto.setDataEnvio(mensagem.getDataEnvio());
        dto.setPublico(mensagem.getPublico());
        dto.setNomeAutor(mensagem.getAutor().getNome());
        return dto;
    }
}