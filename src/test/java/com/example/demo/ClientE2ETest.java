package com.example.demo;


import com.example.demo.model.dto.ClienteRequestDTO;
import com.example.demo.model.dto.ClienteResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/clientes";
    }

    @Test
    void createAndGetClientE2E() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("E2E Juan");
        // set other required fields

        ResponseEntity<ClienteResponseDTO> createResponse = restTemplate.postForEntity(
                baseUrl(), request, ClienteResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long id = createResponse.getBody().getId();

        ResponseEntity<ClienteResponseDTO> getResponse = restTemplate.getForEntity(
                baseUrl() + "/" + id, ClienteResponseDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("E2E Juan", getResponse.getBody().getNombre());
    }

    @Test
    void listClientsE2E() {
        ResponseEntity<ClienteResponseDTO[]> response = restTemplate.getForEntity(
                baseUrl(), ClienteResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void healthEndpointE2E() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cliente Service is UP", response.getBody());
    }
}
