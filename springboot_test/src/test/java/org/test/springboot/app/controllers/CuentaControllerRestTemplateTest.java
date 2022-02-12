package org.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.models.TransaccionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerRestTemplateTest {

    /**
     * Injection Of Dependency
     * Attribute RestTemplate - Cliente HTTP
     */
    @Autowired
    private TestRestTemplate client;

    /**
     * Attribute ObjectMapper, inicializado en el @BeforeEach
     */
    private ObjectMapper objectMapper;

    /**
     * Attribute of Port
     * para conocer el puerto Random.
     * Importandolo con la anotacion @LocalServerPort
     */
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Tests.
     */
    @Test
    @Order(1)
    void testTransferencia() throws JsonProcessingException {

//         Context Given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("5000"));

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("mensaje", "Transferencia realizada con exito!");
        response2.put("transaccion", dto);

//        Context When

        /**Pasamos por argumentos la Url/EndPoint. el Objeto a enviar en el RequestBody que se convierte a
         * formato Json automaticamente y el tipo de la clase con la cual queremos obtener la rta/ResponseBody
         * Devolvera un Json de tipo String.
         */
        ResponseEntity<String> response =
                client.postForEntity(crearUri("/api/cuentas/transferencia"), dto, String.class);
        System.out.println(port);

        /**
         * La variable json es el body del response en tipo de dato String.
         */
        String json = response.getBody();
        System.out.println(json);

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con exito!"));
        assertTrue(json.contains("mensaje"));

        assertEquals(objectMapper.writeValueAsString(response2), json);

        /**
         * Comvertimos el json(String) en una estructura JsonNode(jsonNode) para poder navegar
         * entre sus atributos con el metodo path() propio de JsonNode.
         * Utilizando el -> JsonNode jsonNode = objectMapper.readTree(json)
         */
        JsonNode jsonNode = this.objectMapper.readTree(json);
        assertEquals("Transferencia realizada con exito!", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("5000", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());
        assertEquals(1L, jsonNode.path("transaccion").path("bancoId").asLong());
        assertEquals(2L, jsonNode.path("transaccion").path("cuentaDestinoId").asLong());

    }

    @Test
    @Order(2)
    void testDetalle() {

        ResponseEntity<Cuenta> respuesta = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);

        Cuenta cuenta = respuesta.getBody();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertNotNull(cuenta);
        assertEquals(1L, cuenta.getIdCuenta());
        assertEquals("Richard", cuenta.getPersona());
        assertEquals(5000, cuenta.getSaldo().intValue());
        assertEquals(1L, cuenta.getIdCuenta());
        assertEquals(new Cuenta(1L, "Richard", new BigDecimal("5000.00")), cuenta);

    }

    @Test
    @Order(3)
    void testListar() throws JsonProcessingException {

        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);

        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertEquals(2, cuentas.size());

        assertEquals(1L, cuentas.get(0).getIdCuenta());
        assertEquals("Richard", cuentas.get(0).getPersona());
        assertEquals(5000, cuentas.get(0).getSaldo().intValue());

        assertEquals(2L, cuentas.get(1).getIdCuenta());
        assertEquals("Fanny", cuentas.get(1).getPersona());
        assertEquals(10000, cuentas.get(1).getSaldo().intValue());

        assertEquals(new Cuenta(2L, "Fanny", new BigDecimal("10000.00")), cuentas.get(1));


        /**
         * Navegando la Lista de Cuentas con Formanto JsonNode
         * Convirtiendo la Lista de Cuentas a String y luego A JsonNode.
         */
        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        assertEquals(1L, jsonNode.get(0).path("idCuenta").asLong());
        assertEquals("Richard", jsonNode.get(0).path("persona").asText());
        assertEquals(5000, jsonNode.get(0).path("saldo").asInt());

        assertEquals(2L, jsonNode.get(1).path("idCuenta").asLong());
        assertEquals("Fanny", jsonNode.get(1).path("persona").asText());
        assertEquals(10000, jsonNode.get(1).path("saldo").asInt());

    }

    @Test
    @Order(4)
    void testGuardar() {

        Cuenta cuentaNew = new Cuenta(null, "Pepe", new BigDecimal("6000"));

        ResponseEntity<Cuenta> cuentaCreada = client.postForEntity(crearUri("/api/cuentas"), cuentaNew, Cuenta.class);

        assertEquals(HttpStatus.CREATED, cuentaCreada.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, cuentaCreada.getHeaders().getContentType());

        Cuenta cuentaNueva = cuentaCreada.getBody();

        assertNotNull(cuentaNueva);
        assertEquals(3L, cuentaNueva.getIdCuenta());
        assertEquals("Pepe", cuentaNueva.getPersona());
        assertEquals(6000, cuentaNueva.getSaldo().intValue());

        /**
         * Listando la nueva cuenta Creada
         */
        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());

        assertEquals(3, cuentas.size());
    }

    @Test
    @Order(5)
    void testEliminar() {

        client.delete(crearUri("/api/cuentas/3"));

        /**
         * Otra manera de Eliminar y poder Validar:
         *
         * Parametros ->
         *
         * exchange = client.exchange(crearUri("/api/cuentas/3") -> con el id indicado en el EndPoint
         *              HttpMethod.DELETE, null, Void.class); -> metodo Http(get, Delete)
         *                                                  -> null para las Cabeceras/Headers
         *                                                  -> Valor de retorno Delete=Void / Get=Cuenta
         */
        /**ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/3"),
         HttpMethod.DELETE, null, Void.class);
         assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
         assertFalse(exchange.hasBody());


         Map<String, Long> pathVariable = new HashMap<>();
         pathVariable.put("id", 3L);
         ResponseEntity<Void> exchange1 = client.exchange(crearUri("/api/cuentas/{id}"),
         HttpMethod.DELETE, null, Void.class, pathVariable);
         assertEquals(HttpStatus.NO_CONTENT, exchange1.getStatusCode());
         assertFalse(exchange1.hasBody());
         */

        ResponseEntity<Cuenta[]> lista = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertEquals(HttpStatus.OK, lista.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, lista.getHeaders().getContentType());
        List<Cuenta> bodyList = Arrays.asList(lista.getBody());
        assertEquals(2, bodyList.size());

        ResponseEntity<Cuenta> responseDetalle = client.getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, responseDetalle.getStatusCode());
        assertFalse(responseDetalle.hasBody());

    }

    /**
     * Automatizando el EndPoint Base para cada metodo del controller.
     * Para conocer el port.
     *
     * @param uri
     * @return
     */
    private String crearUri(String uri) {
        return "http://localhost:" + port + uri;
    }
}