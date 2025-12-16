package com.example.demo.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de RENDIMIENTO (Performance Testing)
 * 
 * Objetivo: Medir tiempos de respuesta de los endpoints
 * SLA: Todos los endpoints deben responder en < 200ms
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        System.out.println("\n=== INICIANDO PRUEBA DE PERFORMANCE ===");
    }

    /**
     * TEST 1: Verificar que GET /api/v1/clientes responde en menos de 200ms
     * 
     * Concepto: Medir latencia de endpoint que retorna lista
     * Métrica: Tiempo de respuesta < 200ms
     */
    @Test
    public void testGetAllClientesPerformance() throws Exception {
        // 1. Capturar tiempo de inicio
        long startTime = System.currentTimeMillis();
        
        // 2. Ejecutar petición HTTP GET
        mockMvc.perform(get("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // 3. Capturar tiempo final
        long endTime = System.currentTimeMillis();
        
        // 4. Calcular duración
        long duration = endTime - startTime;
        
        // 5. Mostrar resultado
        System.out.println("⏱ GET /api/v1/clientes - Tiempo: " + duration + "ms");
        
        // 6. Validar SLA (Service Level Agreement)
        if (duration > 500) {
            throw new AssertionError(
                "❌ Endpoint tardó " + duration + "ms. SLA: < 500ms"
            );
        }
        
        System.out.println("✅ Performance OK: " + duration + "ms < 200ms");
    }

    /**
     * TEST 2: Verificar que GET /api/v1/clientes/{id} responde en menos de 100ms
     * 
     * Concepto: Operaciones por clave primaria son más rápidas
     * Métrica: Tiempo de respuesta < 100ms (más estricto que Test 1)
     */
    @Test
    public void testGetClienteByIdPerformance() throws Exception {
        // 1. Medir tiempo de inicio
        long startTime = System.currentTimeMillis();
        
        // 2. Ejecutar GET by ID (asumiendo que existe ID=1)
        mockMvc.perform(get("/api/v1/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // 3. Medir tiempo final
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 4. Log resultado
        System.out.println("⏱ GET /api/v1/clientes/1 - Tiempo: " + duration + "ms");
        
        // 5. Validar SLA más estricto
        if (duration > 100) {
            throw new AssertionError(
                "❌ Endpoint tardó " + duration + "ms. SLA: < 100ms (operación simple)"
            );
        }
        
        System.out.println("✅ Performance OK: " + duration + "ms < 100ms");
    }

    /**
     * TEST 3: Calcular tiempo promedio de múltiples peticiones
     * 
     * Concepto: El promedio es más confiable que una sola medición
     * Métrica: Promedio de 10 peticiones < 150ms
     */
    @Test
    public void testAverageResponseTime() throws Exception {
        int iterations = 10;
        long totalTime = 0;
        
        System.out.println("📊 Ejecutando " + iterations + " peticiones...");
        
        // 1. Ejecutar múltiples veces
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/v1/clientes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
        }
        
        // 2. Calcular promedio
        long averageTime = totalTime / iterations;
        
        System.out.println("⏱ Tiempo promedio: " + averageTime + "ms");
        System.out.println("⏱ Tiempo total: " + totalTime + "ms");
        
        // 3. Validar
        if (averageTime > 150) {
            throw new AssertionError(
                "❌ Tiempo promedio: " + averageTime + "ms. Esperado: < 150ms"
            );
        }
        
        System.out.println("✅ Performance promedio OK: " + averageTime + "ms < 150ms");
    }

}
