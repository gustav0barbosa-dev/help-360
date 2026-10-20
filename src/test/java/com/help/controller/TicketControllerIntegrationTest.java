package com.help.controller;

import com.help.dto.request.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Não usa @Autowired, instancia direto
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveFazerLoginERetornarToken() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("admin@help360.com");
        login.setSenha("Admin@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.usuario.email").value("admin@help360.com"));
    }

    @Test
    void deveBloquearAcessoAoEndpointSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAcessarEndpointComTokenValido() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("admin@help360.com");
        login.setSenha("Admin@123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/v1/tickets/meus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornarErro400AoEnviarTicketSemTitulo() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("admin@help360.com");
        login.setSenha("Admin@123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        String bodyInvalido = """
                {
                    "descricao": "Teste",
                    "prioridade": "URGENTE",
                    "empresaId": 1,
                    "areaId": 1,
                    "sistemaId": 1,
                    "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes").isArray());
    }
}