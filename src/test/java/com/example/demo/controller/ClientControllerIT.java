package com.example.demo.controller;



import com.example.demo.model.dto.ClienteRequestDTO;
import com.example.demo.model.dto.ClienteResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearCliente() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan");
        // set other required fields

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testObtenerClientePorId() throws Exception {
        // First, create a client
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Maria");
        // set other required fields

        String response = mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ClienteResponseDTO created = objectMapper.readValue(response, ClienteResponseDTO.class);

        mockMvc.perform(get("/api/v1/clientes/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria"));
    }

    @Test
    void testListarClientes() throws Exception {
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
