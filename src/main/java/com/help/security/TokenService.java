package com.help.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.help.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Chave secreta que assina o token (depois coloque no application.properties)
    @Value("${api.security.token.secret:minha-chave-super-secreta-help360}")
    private String secret;

    // Gera o token JWT com o e-mail do usuário
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("help360")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())          // Guardamos o ID
                    .withClaim("perfil", usuario.getPerfil().name())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    // Valida o token e retorna o e-mail (subject) se for válido
    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("help360")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return ""; // Token inválido
        }
    }

    // Token expira em 2 horas (configurável)
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}