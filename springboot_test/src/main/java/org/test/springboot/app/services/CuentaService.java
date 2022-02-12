package org.test.springboot.app.services;

import org.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    /**
     * Methods
     */
    List<Cuenta> findAll();

    Cuenta findById(Long idCuenta);

    Cuenta save(Cuenta cuenta);

    void deleteById(Long id);

    int verTotalTransferencias(Long idBanco);

    BigDecimal verSaldo(Long idCuenta);

    void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto, Long idBanco);




}
