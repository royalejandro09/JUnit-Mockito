package org.test.springboot.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.repositories.CuentaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Anotacion para nuestras pruebas unitarias de Integracion con Jpa
 * Habilitatodo el contexto de persistencia la base de datos en memoria..
 */
@Tag("integracion_jpa")
@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals(1L, cuenta.orElseThrow().getIdCuenta());
        assertEquals("Richard", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuentaPersona = cuentaRepository.findByPersona("Fanny");
        assertTrue(cuentaPersona.isPresent());
        assertEquals("Fanny", cuentaPersona.orElseThrow().getPersona());
        assertEquals("5000.00", cuentaPersona.orElseThrow().getSaldo().toPlainString());
        assertEquals(5000, cuentaPersona.orElseThrow().getSaldo().intValue());
    }

    @Test
    void testFindByPersonaThrows() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Roy");
        /**
         * Dos maneras de escribir la Exepcion con el asserTrhows()
         */
        //        assertThrows(NoSuchElementException.class, () -> {
//            cuenta.orElseThrow();     });
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);

        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuenta = cuentaRepository.findAll();
        assertFalse(cuenta.isEmpty());
        assertEquals(2, cuenta.size());

    }

    @Test
    void testSave() {
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        Cuenta cuentaSave = cuentaRepository.save(cuentaPepe); //Ya esta persistida en la base de datos con un Id.
//        Cuenta cuenta1 = cuentaRepository.findById(save.getIdCuenta()).orElseThrow();
//        Cuenta cuenta1 = cuentaRepository.findByPersona("Pepe").orElseThrow();

        assertEquals(3L, cuentaSave.getIdCuenta());//No es recomendado ya que el ID puede ser diferente.
        assertEquals("Pepe", cuentaSave.getPersona());
        assertEquals(3000, cuentaSave.getSaldo().intValue());
    }


    @Test
    void testUpdate() {
        //Id en null ya que es AutoIncremental.
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //Cuenta creada y almacenada en la base de datos.
        Cuenta cuentaSave = cuentaRepository.save(cuentaPepe); //Ya esta persistida en la base de datos con un Id.

//        assertEquals(3L, cuentaSave.getIdCuenta());//No es recomendado ya que el ID puede ser diferente.
        assertEquals("Pepe", cuentaSave.getPersona());
        assertEquals(3000, cuentaSave.getSaldo().intValue());

        //Cuenta almacenada en la base de datos modificada y actualizada en la base de datos.
        cuentaSave.setSaldo(new BigDecimal("6000"));
        Cuenta cuentaUpdate = cuentaRepository.save(cuentaSave);


//        assertEquals(3L, cuentaUpdate.getIdCuenta());
        assertEquals("Pepe", cuentaUpdate.getPersona());
        assertEquals(6000, cuentaUpdate.getSaldo().intValue());

    }

    @Test
    void testDelete() {

        Cuenta cuentaFanny = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Fanny", cuentaFanny.getPersona());

        cuentaRepository.delete(cuentaFanny);

        assertThrows(NoSuchElementException.class, () -> {
//            cuentaRepository.findByPersona("Fanny").orElseThrow();
            cuentaRepository.findById(2L).orElseThrow();

            assertEquals(1, cuentaRepository.findAll().size());
        });
    }
}
