package org.test.springboot.app.models;

import java.math.BigDecimal;

public class TransaccionDto {

    /**
     * Attributes.
     */
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private Long BancoId;
    private BigDecimal monto;

    /**
     * Getter & Setter
     */
    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(Long cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(Long cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public Long getBancoId() {
        return BancoId;
    }

    public void setBancoId(Long bancoId) {
        BancoId = bancoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
