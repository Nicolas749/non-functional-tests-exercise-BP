package com.example.demo.security;

import com.example.demo.model.dto.ClienteRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de SEGURIDAD (Security Testing)
 * 
 * Objetivo: Detectar vulnerabilidades y validar datos de entrada
 * Categorías: Injection, XSS, Validación de datos
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        System.out.println("\n🔒 INICIANDO PRUEBA DE SEGURIDAD");
    }

    /**
     * TEST 1: Verificar protección contra SQL Injection
     * 
     * Ataque: Intentar inyectar código SQL en el nombre
     * Resultado esperado: Sistema RECHAZA (400 Bad Request)
     * Protección: @Pattern bloquea caracteres especiales + JPA usa prepared statements
     */
    @Test
    void testSQLInjectionPrevention() throws Exception {
        System.out.println("🛡 Test: SQL Injection Protection");
        
        // 1. Crear payload malicioso con SQL injection
        ClienteRequestDTO maliciousClient = new ClienteRequestDTO();
        maliciousClient.setNombre("'; DROP TABLE clientes; --");
        maliciousClient.setEmail("hacker@test.com");
        
        // 2. Intentar crear cliente - DEBE SER RECHAZADO
        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maliciousClient)))
                .andExpect(status().isBadRequest()); // Rechazado por caracteres especiales
        
        System.out.println("✅ SQL Injection prevención: BLOQUEADO por validación @Pattern");
    }

    /**
     * TEST 2: Verificar protección contra XSS (Cross-Site Scripting)
     * 
     * Ataque: Intentar inyectar JavaScript en el nombre
     * Resultado esperado: Sistema RECHAZA (400 Bad Request)
     * Protección: @Pattern bloquea caracteres HTML/JS (< > { } [ ])
     */
    @Test
    void testXSSPrevention() throws Exception {
        System.out.println("🛡 Test: XSS Protection");
        
        // 1. Crear payload con script malicioso
        ClienteRequestDTO xssClient = new ClienteRequestDTO();
        xssClient.setNombre("<script>alert('XSS')</script>");
        xssClient.setEmail("xss@test.com");
        
        // 2. Intentar crear cliente - DEBE SER RECHAZADO
        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssClient)))
                .andExpect(status().isBadRequest()); // Rechazado por caracteres especiales
        
        System.out.println("✅ XSS Prevention: BLOQUEADO por validación @Pattern");
    }

}
