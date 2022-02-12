package org.test.springboot.app.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.test.springboot.app.Data.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.test.springboot.app.Data;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.models.TransaccionDto;
import org.test.springboot.app.services.CuentaService;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Anotacion @WebMvcTest contiene el contexto de Test para la clase
 * Controller.
 */
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    /**
     * Injection of dependency con el Attribute (MockMvc)
     * es la implementacion de Mockito para probar un Controlador.
     * Ya viene Configurado Automaticamente con todos los contextos de prueba en Web para el Controller.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Definicion del @MockBean CuentaService, ya que el Controller depende de este.
     */
    @MockBean
    private CuentaService cuentaService;

    /**
     * Attribute ObjectMapper
     * Convertir Objetos a formato String/Json
     * objectMapper.writeValueAsString(Obj);
     */
    ObjectMapper objectMapper;

    /**
     * Method BeforeEach() del ciclo de vida
     * Para Inicializar el ObjectMapper
     */
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Methods Test
     */

    @Test
    void testDetalle() throws Exception {
        //Given
        when(cuentaService.findById(1l)).thenReturn(crearCuenta1().orElseThrow());

        //When
        /**
         * Aqui se realiza la llamada al controlador mediante la ruta/EndPoint
         *
         * mvc.perform() -> llevar a cabo, realizar o invocar la ruta URL.
         *
         * -importamos la clase (MockMvcRequestBuilders) de forma estatica. -> MockMvcRequestBuilders.get()
         * usa los distintos tipos de Request como Get, Post, Put, Delete...
         * ejemplo get("api/cuentas/1")
         *
         * -ContentType que lo que se envie en el cuerpo del Request sea un JSON ->ContentType(MediaType.APPLICATION_JSON)
         * -andExpect() -> Esperar un resultado para trabajar con la respuesta
         *
         */
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))

//       Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Richard"))
                .andExpect(jsonPath("$.saldo").value("10000"));

        verify(cuentaService).findById(1l);
    }

    @Test
    void testTransferencia() throws Exception {

        /**
         * Contexto Given
         *
         * Creando el Objeto dto el cual se convertira en JSON y se pasara como parametro.
         * en el RequestBody o cuerpo de la peticion.
         */
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("8000"));
        dto.setBancoId(1L);
//        System.out.println(objectMapper.writeValueAsString(dto)); //JSON

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);
//        System.out.println(objectMapper.writeValueAsString(response));


//        Contexto When
        /**
         * +ContentType -> Lo que se envia en el cuerpo del Request sea un JSON o sea el Parametro
         * Objeto dto convertido a JSON.
         *
         * +Content -> contenido/objeto que estamos enviando en el RequestBody o cuerpo de la solicitud.
         * recibe un String o Byte de nuestro Objeto que debemos retornar en JSON, por eso lo convertimos usando:
         *  el atributo (ObjectMapper) y lo incluimos en el metodo
         *  BeforeEach() del ciclo de vida para inicializarlo.
         *  .contente(objectMapper.writeValueAsString(dto)
         */
        mvc.perform(post("/api/cuentas/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) //Contenido enviado en el RequestBody/Prametro convertido a String

//                Context Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito!"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));


    }

    @Test
    void testListar() throws Exception {

//        Contexto Given

        /**
         * Dos maneras de crear la List de cuentas.
         * Importandolas de la clase Data.
         */
//        List<Cuenta> cuentas = Arrays.asList(CUENTA_001, CUENTA_002);
        //Al ser un Array/Arreglo, los indices de cada cuenta estaran
        // en el orden en que se agregan a la lista.
        List<Cuenta> cuentas = Arrays.asList(crearCuenta1().orElseThrow(),
                crearCuenta2().orElseThrow());

        when(cuentaService.findAll()).thenReturn(cuentas);

//        Contexto When

        mvc.perform(get("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON))

//        Contexto Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                /**
                 * JSON que devuelve el metodo findAll()
                 * es un arreglo de 2 cuentas.
                 */
                .andExpect(jsonPath("$[0].persona").value("Richard"))
                .andExpect(jsonPath("$[1].persona").value("Fanny"))
                .andExpect(jsonPath("$[0].saldo").value("10000"))
                .andExpect(jsonPath("$[1].saldo").value("5000"))
                .andExpect(jsonPath("$", hasSize(2))) //tamaño del Array/Arreglo
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));//El valor esperado debe ser el mismo que el almacendado en la variable cuentas.

        verify(cuentaService).findAll();
    }

    @Test
    void testAlmacenar() throws Exception {

//        Contexto Given
        Cuenta cuentaRoy = new Cuenta(null, "Roy", new BigDecimal("20000"));

        when(cuentaService.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setIdCuenta(3L);
            return c;
        });

//        Contexto When
        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRoy))) //pasando la cuentaRoy en el requestBody convertida en String/Format JSON

//        Context Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idCuenta", is(3)))
                .andExpect(jsonPath("$.persona", is("Roy")))
                .andExpect(jsonPath("$.saldo", is(20000)));
        verify(cuentaService).save(any());

    }
}