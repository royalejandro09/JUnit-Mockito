package org.test.springboot.app.controllers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.models.TransaccionDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

/**
 * Forma de dar un orden a la ejecucion de los metodos.
 * con @TestMethodOrder.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

/**
 * @SpringBoottest -> Contiene el contexto de Test para la clase controller real :
 * WebTestClient, para pruebas de Integracion
 * Anotacion Random_Port -> para definir un puerto automatico que este disponible para prueba
 * y levanta un servidor real.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Tag("integracion_wc")
class CuentaControllerWebTestClientTest {

    private ObjectMapper objectMapper;

    /**
     * Attribute Injection of Dependency con @Autowired
     * Debemos tener en el Pom.xml (WebFlux)
     * WebTestClient
     */
    @Autowired
    private WebTestClient client;

    /**
     * Icicializar en cada metodo Test.
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferencia() throws JsonProcessingException {
//Context Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("5000"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);

//        Context When
        client.post().uri("/api/cuentas/transferencia")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
//                Context Exchange
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody() //Por defecto es tipo de dato Byte modificamos a .expectBody(String.class)
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito!", json.path("mensaje").asText());
                        assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals(5000, json.path("transaccion").path("monto").asInt());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con exito!"))
                .jsonPath("$.mensaje").value(valor ->
                        assertEquals("Transferencia realizada con exito!", valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito!")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));


    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {

        Cuenta cuenta = new Cuenta(1L, "Richard", new BigDecimal("5000"));

        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Richard")
                .jsonPath("$.saldo").isEqualTo(5000)
                .json(objectMapper.writeValueAsString(cuenta));


    }

    @Test
    @Order(3)
    void testDetalle2() {

        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuenta = response.getResponseBody();
                    assertNotNull(cuenta);
                    assertEquals("Fanny", cuenta.getPersona());
                    assertEquals(10000, cuenta.getSaldo().intValue());

                });
    }

    @Test
    @Order(4)
    void testListar() {
        client.get().uri("/api/cuentas").exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Richard")
                .jsonPath("$[0].saldo").isEqualTo(5000)
                .jsonPath("$[0].idCuenta").isEqualTo(1)
                .jsonPath("$[1].persona").isEqualTo("Fanny")
                .jsonPath("$[1].saldo").isEqualTo(10000)
                .jsonPath("$[1].idCuenta").isEqualTo(2)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void testListar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals(1L, cuentas.get(0).getIdCuenta());
                    assertEquals("Richard", cuentas.get(0).getPersona());
                    assertEquals(5000, cuentas.get(0).getSaldo().intValue());

                    assertEquals(2L, cuentas.get(1).getIdCuenta());
                    assertEquals("Fanny", cuentas.get(1).getPersona());
                    assertEquals(10000, cuentas.get(1).getSaldo().intValue());
                })
                .hasSize(2)
                .value(hasSize(2));
    }

    @Test
    @Order(6)
    void testGuardar() {

//        Context Given
        Cuenta cuenta3 = new Cuenta(null, "Pepe", new BigDecimal("7000"));

//        Context When
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta3)
                .exchange()

//                Context Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.idCuenta").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.persona").value(is("Pepe"))
                .jsonPath("$.saldo").isEqualTo(7000);

    }

    @Test
    @Order(7)
    void testGuardar2() {

//        Context Given
        Cuenta cuenta3 = new Cuenta(null, "Pepa", new BigDecimal("9000"));

//        Context When
        client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta3)
                .exchange()

//                Context Then
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4, c.getIdCuenta());
                    assertEquals("Pepa", c.getPersona());
                    assertEquals(9000, c.getSaldo().intValue());
                });


    }

    @Test
    @Order(8)
    void testEliminar() {

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}