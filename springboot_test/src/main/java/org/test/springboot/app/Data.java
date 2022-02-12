package org.test.springboot.app;

import net.bytebuddy.dynamic.DynamicType;
import org.test.springboot.app.models.Banco;
import org.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Data {

    /**
     * Attribute static
     */
    public static final Cuenta CUENTA_001 =
            new Cuenta(1L, "Richard", new BigDecimal("10000"));

    public static final Cuenta CUENTA_002 =
            new Cuenta(2L, "Fanny", new BigDecimal("5000"));

    public static final Banco BANCO_1 =
            new Banco(1L, "Banco de la Rpublica", 0);

    /**
     * Metodos static que van a crear nuevas instancias cada vez que sean invocados.
     */
    public static Optional<Cuenta> crearCuenta1() {
        return Optional.of(new Cuenta(1L, "Richard", new BigDecimal("10000")));
    }

    public static Optional<Cuenta> crearCuenta2() {
        return Optional.of(new Cuenta(2L, "Fanny", new BigDecimal("5000")));
//        return new Cuenta(1L, "Richard", new BigDecimal("10000"));
    }

    public static Optional<Banco> crearBanco() {

        return Optional.of(new Banco(1L, "Banco de la Rpublica", 0));
    }


}
