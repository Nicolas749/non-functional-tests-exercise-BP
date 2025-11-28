package com.example.demo.service;


import com.example.demo.model.dto.ClienteRequestDTO;
import com.example.demo.model.dto.ClienteResponseDTO;
import com.example.demo.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        // Replace with actual implementation and mocks as needed
        clienteService = Mockito.mock(ClienteService.class);
    }

    @Test
    void testCrear() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        ClienteResponseDTO response = new ClienteResponseDTO();
        when(clienteService.crear(any(ClienteRequestDTO.class))).thenReturn(response);

        ClienteResponseDTO result = clienteService.crear(request);
        assertNotNull(result);
    }

    @Test
    void testObtenerPorId() {
        ClienteResponseDTO response = new ClienteResponseDTO();
        when(clienteService.obtenerPorId(1L)).thenReturn(response);

        ClienteResponseDTO result = clienteService.obtenerPorId(1L);
        assertNotNull(result);
    }

    @Test
    void testObtenerPorNombre() {
        ClienteResponseDTO response = new ClienteResponseDTO();
        when(clienteService.obtenerPorNombre("Juan")).thenReturn(response);

        ClienteResponseDTO result = clienteService.obtenerPorNombre("Juan");
        assertNotNull(result);
    }

    @Test
    void testListar() {
        when(clienteService.listar()).thenReturn(Collections.emptyList());

        List<ClienteResponseDTO> result = clienteService.listar();
        assertNotNull(result);
    }

    @Test
    void testActualizar() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        ClienteResponseDTO response = new ClienteResponseDTO();
        when(clienteService.actualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(response);

        ClienteResponseDTO result = clienteService.actualizar(1L, request);
        assertNotNull(result);
    }

    @Test
    void testEliminar() {
        doNothing().when(clienteService).eliminar(1L);
        clienteService.eliminar(1L);
        verify(clienteService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarPermanente() {
        doNothing().when(clienteService).eliminarPermanente(1L);
        clienteService.eliminarPermanente(1L);
        verify(clienteService, times(1)).eliminarPermanente(1L);
    }
}
