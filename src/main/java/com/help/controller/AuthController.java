package com.help.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.help.dto.request.AlterarSenhaRequestDTO;
import com.help.dto.request.LoginRequestDTO;
import com.help.dto.response.LoginResponseDTO;
import com.help.model.Usuario;
import com.help.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<Void> alterarSenha(
            @RequestBody @Valid AlterarSenhaRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        authService.alterarSenha(dto, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }

}