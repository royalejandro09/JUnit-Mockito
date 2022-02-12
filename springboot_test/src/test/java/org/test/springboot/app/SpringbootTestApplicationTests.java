package org.test.springboot.app;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.test.springboot.app.Data.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.test.springboot.app.exceptions.DineroInsuficienteException;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.repositories.BancoRepository;
import org.test.springboot.app.repositories.CuentaRepository;
import org.test.springboot.app.services.CuentaService;
import org.test.springboot.app.services.CuentaServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class SpringbootTestApplicationTests {

    /**
     * Creacion de atributos para el uso de los Mock y el Service.
     */
//    CuentaRepository cuentaRepository;
//    BancoRepository bancoRepository;
//    CuentaService service;

    /**
     * Usando las anotaciones de Spring
     *
     * @MockBean
     * @Autowired -> Usado con la clase service depues de anotarla con @Service para
     * almacenarla como un componente en el contenedor de Spring.
     */
    @MockBean
    CuentaRepository cuentaRepository;
    @MockBean
    BancoRepository bancoRepository;
    @Autowired
    CuentaService service;

    /**
     * Utilizando las anotaciones de Mockito
     *
     * @Mock
     * @InjectsMock -> A la clase Service implementada (CuentaServiceImpl)
     * para injectar por medio del constructor los Mocks.
     */
//    @Mock
//    CuentaRepository cuentaRepository;
//    @Mock
//    BancoRepository bancoRepository;
//    @InjectMocks
//    CuentaServiceImpl service;


    /**
     * Importado metodos static de la clase Data para el uso de metodos y variables de forma directa:
     * import static org.test.springboot.app.Data.*;
     *
     * Methods -> crearCuenta1, crearCuenta2, crearCuentaBanco
     */

    /**
     * Method BeforeEach
     * para instanciar/definir de forma estatica los Mock y la instancia del Service.
     */
    @BeforeEach
    void setUp() {
//        cuentaRepository = mock(CuentaRepository.class);
//        bancoRepository = mock(BancoRepository.class);
//        service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
        /**
         * Para que en cada uno de los metodos no se modifiquen los datos seran reiniciados aqui:

         Data.CUENTA_001.setSaldo(new BigDecimal("10000"));
         Data.CUENTA_002.setSaldo(new BigDecimal("5000"));
         Data.BANCO_1.setTotalTransferencias(0);
         */
    }

    /**
     * Methods Test de Pruebas.
     */
    @Test
    void contextLoads() {

        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta1());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta2());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = service.verSaldo(1L);
        BigDecimal saldoDestino = service.verSaldo(2L);

        assertEquals("10000", saldoOrigen.toPlainString());
        assertEquals("5000", saldoDestino.toPlainString());

        service.transferir(1L, 2L, new BigDecimal("375"), 1L);

        saldoOrigen = service.verSaldo(1L);
        saldoDestino = service.verSaldo(2L);
        int cantidadTransferencias = service.verTotalTransferencias(1L);

        assertEquals("9625", saldoOrigen.toPlainString());
        assertEquals("5375", saldoDestino.toPlainString());
        assertEquals(1, cantidadTransferencias);

        verify(bancoRepository, times(2)).findById(1L);
        verify(cuentaRepository, times(2)).save(any());
        verify(bancoRepository).save(any());
        verify(cuentaRepository, times((3))).findById(1L);
        verify(cuentaRepository, times((3))).findById(2L);

    }

    @Test
    void contextLoads2() {

        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta1());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta2());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = service.verSaldo(1L);
        BigDecimal saldoDestino = service.verSaldo(2L);

        assertEquals("10000", saldoOrigen.toPlainString());
        assertEquals("5000", saldoDestino.toPlainString());

//        service.transferir(1L, 2L, new BigDecimal("375"), 1L);
        /**
         * Lanzamos la Excepcion para que pase el test con:
         * assertThrows(DineroInsuficienteException.class, () ->...
         */
        assertThrows(DineroInsuficienteException.class, () -> {
            service.transferir(1L, 2L, new BigDecimal("15000"), 1L);
        });

        saldoOrigen = service.verSaldo(1L);
        saldoDestino = service.verSaldo(2L);
        int cantidadTransferencias = service.verTotalTransferencias(1L);

        assertEquals("10000", saldoOrigen.toPlainString());
        assertEquals("5000", saldoDestino.toPlainString());
        assertEquals(0, cantidadTransferencias);

        verify(bancoRepository).findById(1L);
        verify(cuentaRepository, never()).save(any());
        verify(bancoRepository, never()).save(any());
        verify(cuentaRepository, times((3))).findById(1L);
        verify(cuentaRepository, times((2))).findById(2L);

    }

    @Test
    void contexLoad3() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta1());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta2());

        Cuenta cuenta1 = service.findById(1L);
        Cuenta cuenta2 = service.findById(2L);

        /**
         * Utilizando el assertSame Compara Instancias
         */
//        assertSame(cuenta1, cuenta2); //falla por que son Diferentes Instancias/Compara Instancias.
        assertEquals("Fanny", cuenta2.getPersona());
//        assertTrue(cuenta1.equals(cuenta2)); //no revisa instancias solo datos, Falla si deshabilito el metodo Equals el cual compara los datos
//        assertEquals(cuenta1, cuenta2); //Compara datos no Instancias
        assertNotEquals(cuenta1, cuenta2); //Igual compara Datos de la cuenta con el Equals, no la instancia.

        verify(cuentaRepository, times(2)).findById(any());

    }

    @Test
    void testFindAll() {

//        Context Given
        List<Cuenta> datos = Arrays.asList(crearCuenta1().orElseThrow(),
                crearCuenta2().orElseThrow());

        when(cuentaRepository.findAll()).thenReturn(datos);

//        Context When
        List<Cuenta> cuentas = service.findAll();

//        Context Then
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(crearCuenta2().orElseThrow())); //Compara valores de los atributos por el Equals.

        verify(cuentaRepository).findAll();


    }

    @Test
    void testSave() {

//        Context Given
        Cuenta cuentaRoy = new Cuenta(null, "Roy", new BigDecimal("20000"));

        when(cuentaRepository.save(any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setIdCuenta(3L);
            return c;

        });

//        Context When
        Cuenta cuentax = service.save(cuentaRoy);

//        Context Then
        assertEquals("Roy", cuentax.getPersona());
        assertEquals(3L, cuentax.getIdCuenta());
        assertEquals(20000, cuentax.getSaldo().intValue());

        verify(cuentaRepository, times(1)).save(any());


    }
}
