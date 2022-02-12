package org.test.springboot.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.springboot.app.models.Banco;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.repositories.BancoRepository;
import org.test.springboot.app.repositories.CuentaRepository;

import java.math.BigDecimal;
import java.util.List;

@Service //Haciendo la clase como Componente de Spring guardandola en el contenedor.
public class CuentaServiceImpl implements CuentaService {

    /**
     * Injection Of Dependency
     * Repository -> acceden a datos (DAO) consultas a bases de datos...
     */
    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    /**
     * Injection of dependency by constructor
     * para que se inyecten automaticamente cuando utilicemos las anotaciones con Mockito @Mockito
     */
    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    /**
     * Implements Of Methods
     */

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long idCuenta) {
        return cuentaRepository.findById(idCuenta).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int verTotalTransferencias(Long idBanco) {
        Banco banco = bancoRepository.findById(idBanco).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal verSaldo(Long idCuenta) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta).orElseThrow();
        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto, Long idBanco) {

        Cuenta cuentaOrigen = cuentaRepository.findById(idCuentaOrigen).orElseThrow();
        cuentaOrigen.debito(monto);
        cuentaRepository.save(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(idCuentaDestino).orElseThrow();
        cuentaDestino.credito(monto);
        cuentaRepository.save(cuentaDestino);

        Banco banco = bancoRepository.findById(idBanco).orElseThrow();
        int cantidadTransferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++cantidadTransferencias);
        bancoRepository.save(banco);
    }


}
