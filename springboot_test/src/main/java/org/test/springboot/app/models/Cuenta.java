package org.test.springboot.app.models;

import org.test.springboot.app.exceptions.DineroInsuficienteException;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    /**
     * Attribute
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuenta;
    private String persona;
    private BigDecimal saldo;

    /**
     * Constructor
     */
    public Cuenta() {
    }

    public Cuenta(Long idCuenta, String persona, BigDecimal saldo) {
        this.idCuenta = idCuenta;
        this.persona = persona;
        this.saldo = saldo;
    }

    /**
     * Methods Getter & Setter
     */
    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    /**
     * Methods de la clase/comportamiento
     */
    public void debito(BigDecimal cobro) {
        BigDecimal nuevoSaldo = saldo.subtract(cobro);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException
                    ("El saldo para esta transaccion es insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal deposito) {
        this.saldo = saldo.add(deposito);
    }


    /**
     * Method Equals() & hasHcode()
     * Comparar objetos con atributos similares .
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(idCuenta, cuenta.idCuenta) &&
                Objects.equals(persona, cuenta.persona) && Objects.equals(saldo, cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCuenta, persona, saldo);
    }
}
