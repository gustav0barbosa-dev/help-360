package com.help.exception;

import com.help.dto.response.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.swagger.v3.oas.annotations.Hidden;

import java.time.LocalDateTime;
import java.util.List;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura a RecursoNaoEncontradoException (404)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 2. Captura a RegraDeNegocioException (400)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponseDTO> handleRegraDeNegocio(
            RegraDeNegocioException ex, HttpServletRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Regra de negócio violada",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 3. Captura AcessoNegadoException (403)
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponseDTO> handleAcessoNegado(
            AcessoNegadoException ex, HttpServletRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // 4. Captura os erros de validação do @Valid (Ex: campo obrigatório vazio)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        // Pega todos os erros de campo (ex: "Título é obrigatório")
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos. Verifique os detalhes.",
                request.getRequestURI(),
                detalhes
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 5. Captura qualquer outro erro inesperado (500) para não vazar stack trace
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> handleErroGenerico(
            Exception ex, HttpServletRequest request) {
        
        // IMPORTANTE: Em produção, logar esse erro (usando SLF4J) para investigar depois
        // log.error("Erro interno não tratado: ", ex);

        // ISSO VAI IMPRIMIR O ERRO REAL NO CONSOLE
        System.out.println("❌ ERRO INTERNO CAPTURADO: " + ex.getClass().getName());
        System.out.println("❌ MENSAGEM: " + ex.getMessage());
        ex.printStackTrace();

        ErroResponseDTO erro = new ErroResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}