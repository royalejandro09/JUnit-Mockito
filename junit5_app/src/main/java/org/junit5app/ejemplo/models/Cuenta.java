package org.junit5app.ejemplo.models;

import org.junit5app.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class Cuenta {

    /**
     * Attribute
     */
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    /**
     * Cosntructor
     */
    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    /**
     * Getter & Setter
     */
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

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    /**
     * Methods
     */
    public void debito(BigDecimal monto) {
        BigDecimal newSaldo = saldo.subtract(monto);

        if (newSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero Insuficiente");
        }
        this.saldo = newSaldo;
    }


    public void credito(BigDecimal monto) {
        this.saldo = saldo.add(monto);
    }


    /**
     * Se hace la comparacion de objetos con el metodo equals para saber si tienen los mismos datos
     *
     * @param o
     * @return
     */
    @Override
//Validacion mediante la instancia.
    public boolean equals(Object o) {
        if (!(o instanceof Cuenta)) {
            return false;
        }
//Casteo del objeto "o" de tipo Object recibido como parametro
        Cuenta c = (Cuenta) o;
        if (this.persona == null || this.saldo == null) {
            return false;
        }

        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }

}
