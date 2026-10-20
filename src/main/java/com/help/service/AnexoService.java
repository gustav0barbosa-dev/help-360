package com.help.service;

import com.help.dto.response.AnexoResponseDTO;
import com.help.exception.RecursoNaoEncontradoException;
import com.help.exception.RegraDeNegocioException;
import com.help.model.Anexo;
import com.help.model.Ticket;
import com.help.model.Usuario;
import com.help.repository.AnexoRepository;
import com.help.repository.TicketRepository;
import com.help.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnexoService {

    private final AnexoRepository anexoRepository;
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    private static final List<String> EXTENSOES_PERMITIDAS = List.of(
            "pdf", "png", "jpg", "jpeg", "gif", "doc", "docx", "xls", "xlsx", "txt", "zip"
    );

    @Transactional
    public AnexoResponseDTO upload(Long ticketId, MultipartFile arquivo, Long autorId) {
        // 1. Validações
        if (arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Arquivo vazio.");
        }
        if (arquivo.getSize() > 10 * 1024 * 1024) {
            throw new RegraDeNegocioException("Arquivo excede o limite de 10 MB.");
        }
        String extensao = getExtensao(arquivo.getOriginalFilename());
        if (!EXTENSOES_PERMITIDAS.contains(extensao.toLowerCase())) {
            throw new RegraDeNegocioException("Extensão não permitida: " + extensao);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado."));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        // 2. Salva o arquivo no disco
        try {
            Path pastaUpload = Paths.get(uploadDir);
            if (!Files.exists(pastaUpload)) {
                Files.createDirectories(pastaUpload);
            }
            String nomeUnico = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path caminhoCompleto = pastaUpload.resolve(nomeUnico);
            Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);

            // 3. Salva no banco
            Anexo anexo = new Anexo();
            anexo.setNomeArquivo(arquivo.getOriginalFilename());
            anexo.setUrlArquivo(nomeUnico);
            anexo.setTipoArquivo(arquivo.getContentType());
            anexo.setTamanhoBytes(arquivo.getSize());
            anexo.setDataUpload(LocalDateTime.now());
            anexo.setTicket(ticket);
            anexo.setAutor(autor);

            return converterParaDTO(anexoRepository.save(anexo));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o arquivo.", e);
        }
    }

    public Resource download(Long id) {
        Anexo anexo = anexoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Anexo não encontrado."));
        try {
            Path caminho = Paths.get(uploadDir).resolve(anexo.getUrlArquivo()).normalize();
            Resource resource = new UrlResource(caminho.toUri());
            if (!resource.exists()) {
                throw new RecursoNaoEncontradoException("Arquivo não encontrado no servidor.");
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar arquivo.", e);
        }
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Anexo anexo = anexoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Anexo não encontrado."));
        
        // Regra: só o autor ou admin pode excluir (RN0044)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        
        boolean isAdmin = usuario.getPerfil().name().equals("ADMIN");
        boolean isAutor = anexo.getAutor().getId().equals(usuarioId);
        
        if (!isAdmin && !isAutor) {
            throw new RegraDeNegocioException("Apenas o autor ou um administrador pode excluir o anexo.");
        }

        try {
            Path caminho = Paths.get(uploadDir).resolve(anexo.getUrlArquivo()).normalize();
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            // Logar, mas não impede a exclusão do banco
        }
        anexoRepository.delete(anexo);
    }

    @Transactional(readOnly = true)
    public List<AnexoResponseDTO> listarPorTicket(Long ticketId) {
        return anexoRepository.findByTicketId(ticketId)
                .stream().map(this::converterParaDTO).toList();
    }

    private String getExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) return "";
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
    }

    private AnexoResponseDTO converterParaDTO(Anexo anexo) {
        AnexoResponseDTO dto = new AnexoResponseDTO();
        dto.setId(anexo.getId());
        dto.setNomeArquivo(anexo.getNomeArquivo());
        dto.setUrlArquivo("/api/v1/anexos/" + anexo.getId() + "/download");
        dto.setTipoArquivo(anexo.getTipoArquivo());
        dto.setTamanhoBytes(anexo.getTamanhoBytes());
        dto.setDataUpload(anexo.getDataUpload());
        dto.setNomeAutor(anexo.getAutor().getNome());
        return dto;
    }
}