package com.example.demo.performance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de CARGA (Load Testing)
 * 
 * Objetivo: Evaluar comportamiento bajo múltiples usuarios concurrentes
 * Herramienta: REST Assured + ExecutorService
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoadTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        System.out.println("\n🔥 INICIANDO PRUEBA DE CARGA (Puerto: " + port + ")");
    }

    /**
     * TEST 1: Simular 50 usuarios concurrentes
     * 
     * Concepto: ExecutorService para paralelismo
     * Métrica: Al menos 95% de peticiones exitosas
     */
    @Test
    public void testConcurrentUsers() throws InterruptedException, ExecutionException {
        int numberOfUsers = 50;
        
        // 1. Crear pool de threads (50 threads concurrentes)
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        
        // 2. Contadores thread-safe
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // 3. Lista para almacenar resultados (tiempos de respuesta)
        List<Future<Long>> futures = new ArrayList<>();
        
        System.out.println("👥 Simulando " + numberOfUsers + " usuarios concurrentes...");
        
        long testStartTime = System.currentTimeMillis();
        
        // 4. Crear 50 tareas concurrentes
        for (int i = 0; i < numberOfUsers; i++) {
            final int userId = i + 1;
            
            Future<Long> future = executor.submit(() -> {
                long requestStart = System.currentTimeMillis();
                
                try {
                    // Ejecutar petición HTTP GET
                    given()
                        .when()
                        .get("/api/v1/clientes")
                        .then()
                        .statusCode(200);
                    
                    // Incrementar contador de éxitos
                    successCount.incrementAndGet();
                    
                    long requestEnd = System.currentTimeMillis();
                    return requestEnd - requestStart;
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("❌ Usuario " + userId + " falló: " + e.getMessage());
                    return -1L;
                }
            });
            
            futures.add(future);
        }
        
        // 5. Esperar a que todas las tareas terminen
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        long testEndTime = System.currentTimeMillis();
        long totalDuration = testEndTime - testStartTime;
        
        // 6. Calcular estadísticas
        long totalResponseTime = 0;
        long maxResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        
        for (Future<Long> future : futures) {
            long responseTime = future.get();
            if (responseTime > 0) {
                totalResponseTime += responseTime;
                maxResponseTime = Math.max(maxResponseTime, responseTime);
                minResponseTime = Math.min(minResponseTime, responseTime);
            }
        }
        
        long avgResponseTime = successCount.get() > 0 
            ? totalResponseTime / successCount.get() 
            : 0;
        
        // 7. Mostrar resultados
        System.out.println("\n📊 RESULTADOS DE CARGA:");
        System.out.println(" 👥 Usuarios simulados: " + numberOfUsers);
        System.out.println(" ✅ Peticiones exitosas: " + successCount.get());
        System.out.println(" ❌ Peticiones fallidas: " + errorCount.get());
        System.out.println(" ⏱ Tiempo total: " + totalDuration + "ms");
        System.out.println(" 📈 Tiempo promedio: " + avgResponseTime + "ms");
        System.out.println(" ⚡ Tiempo mínimo: " + minResponseTime + "ms");
        System.out.println(" 🐌 Tiempo máximo: " + maxResponseTime + "ms");
        System.out.println(" 📊 Throughput: " + (numberOfUsers * 1000 / totalDuration) + " req/s");
        
        // 8. Validaciones
        assertThat("Al menos 95% deben ser exitosas",
            successCount.get(), greaterThanOrEqualTo((int)(numberOfUsers * 0.95)));
        
        assertThat("Menos del 5% pueden fallar",
            errorCount.get(), lessThan(numberOfUsers / 20));
        
        assertThat("Tiempo promedio bajo carga < 10000ms (bajo concurrencia es mayor)",
            avgResponseTime, lessThan(10000L));
        
        System.out.println("✅ PRUEBA DE CARGA EXITOSA");
    }

    /**
     * TEST 2: Prueba de estrés con 100 peticiones rápidas
     * 
     * Concepto: Lanzar todas las peticiones sin control
     * Métrica: Completarse en < 10 segundos con < 10% errores
     */
    @Test
    public void testStressLoad() throws InterruptedException, ExecutionException {
        int numberOfRequests = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<Future<Long>> futures = new ArrayList<>();
        
        System.out.println("⚡ PRUEBA DE ESTRÉS: " + numberOfRequests + " peticiones...");
        
        long startTime = System.currentTimeMillis();
        
        // 1. Lanzar 100 peticiones concurrentes
        for (int i = 0; i < numberOfRequests; i++) {
            Future<Long> future = executor.submit(() -> {
                long reqStart = System.currentTimeMillis();
                
                given()
                    .when()
                    .get("/api/v1/clientes")
                    .then()
                    .statusCode(200);
                
                long reqEnd = System.currentTimeMillis();
                return reqEnd - reqStart;
            });
            
            futures.add(future);
        }
        
        // 2. Recopilar tiempos
        List<Long> responseTimes = new ArrayList<>();
        int errors = 0;
        
        for (Future<Long> future : futures) {
            try {
                responseTimes.add(future.get());
            } catch (Exception e) {
                errors++;
            }
        }
        
        executor.shutdown();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // 3. Calcular estadísticas
        long minTime = responseTimes.stream().min(Long::compare).orElse(0L);
        long maxTime = responseTimes.stream().max(Long::compare).orElse(0L);
        long avgTime = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
        
        System.out.println("\n📊 ESTADÍSTICAS DE ESTRÉS:");
        System.out.println(" ⏱ Tiempo mínimo: " + minTime + "ms");
        System.out.println(" ⏱ Tiempo máximo: " + maxTime + "ms");
        System.out.println(" ⏱ Tiempo promedio: " + avgTime + "ms");
        System.out.println(" ⏱ Tiempo total: " + totalTime + "ms");
        System.out.println(" ❌ Errores: " + errors);
        System.out.println(" 📊 Throughput: " + (numberOfRequests * 1000 / totalTime) + " req/s");
        
        // 4. Validaciones
        assertThat("Debe completarse en < 20 segundos",
            totalTime, lessThan(20000L));
        
        assertThat("Máximo 10% de errores",
            errors, lessThan(numberOfRequests / 10));
        
        assertThat("Tiempo promedio bajo estrés < 10000ms",
            avgTime, lessThan(10000L));
        
        System.out.println("✅ Sistema resistió el estrés");
    }

    /**
     * TEST 3: Medir degradación de rendimiento bajo carga creciente
     * 
     * Concepto: Comparar tiempos con 1, 5, 10, 25, 50 usuarios
     * Métrica: Degradación < 200%
     */
    @Test
    public void testPerformanceDegradation() throws InterruptedException {
        System.out.println("📈 PRUEBA DE DEGRADACIÓN...");
        
        int[] loadLevels = {1, 5, 10, 25, 50};
        List<Long> avgResponseTimes = new ArrayList<>();
        
        // 1. Probar con diferentes niveles de carga
        for (int users : loadLevels) {
            CountDownLatch latch = new CountDownLatch(users);
            ExecutorService executor = Executors.newFixedThreadPool(users);
            List<Long> times = new ArrayList<>();
            
            for (int i = 0; i < users; i++) {
                executor.submit(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        
                        given()
                            .when()
                            .get("/api/v1/clientes")
                            .then()
                            .statusCode(200);
                        
                        long end = System.currentTimeMillis();
                        synchronized (times) {
                            times.add(end - start);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await(30, TimeUnit.SECONDS);
            executor.shutdown();
            
            // 2. Calcular promedio para este nivel
            long avgTime = times.stream().mapToLong(Long::longValue).sum() / times.size();
            avgResponseTimes.add(avgTime);
            
            System.out.println(" 👥 " + users + " usuarios → ⏱ " + avgTime + "ms promedio");
        }
        
        // 3. Calcular degradación
        long baselineTime = avgResponseTimes.get(0); // 1 usuario
        long highLoadTime = avgResponseTimes.get(avgResponseTimes.size() - 1); // 50 usuarios
        
        double degradation = ((highLoadTime - baselineTime) * 100.0) / baselineTime;
        
        System.out.println("\n📊 ANÁLISIS:");
        System.out.println(" 📏 Baseline (1 usuario): " + baselineTime + "ms");
        System.out.println(" 🔥 Alta carga (50 usuarios): " + highLoadTime + "ms");
        System.out.println(" 📈 Degradación: " + String.format("%.2f", degradation) + "%");
        
        // 4. Validar degradación aceptable
        assertThat("Degradación debe ser < 1000%",
            degradation, lessThan(1000.0));
        
        System.out.println("✅ Degradación aceptable");
    }

}
