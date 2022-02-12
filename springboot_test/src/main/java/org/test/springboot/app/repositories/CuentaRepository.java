package org.test.springboot.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.test.springboot.app.models.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long>{

    /**
     * Methods.
     */
//    List<Cuenta> findAll();

//    Cuenta findById(Long idCuenta);

//    void update(Cuenta cuenta);

    /**
     * Dos maneras de implementar la busqueda a la base de datos con un metodo personalizado:
     * 1. @Query (consulta personalizada con @Query sobreescribe el nombre del metodo. (Consulta de JPA).
     *      es una consulta a la clase ->  Entity Cuenta objeto Cuenta.
     *
     * 2. Palabra reservada (findBy) de SpringData.por debajo hace una consulta con el where cuando el atributo
     *  persona es igual a un parametro pasado en el metodo.
     */
    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);

}
