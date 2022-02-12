package org.test.springboot.app.models;

import javax.persistence.*;

@Entity
@Table(name = "bancos")
public class Banco {
    /**
     * Attribute
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBanco;
    private String nombre;
    @Column(name = "total_transferencias")
    private int totalTransferencias;

    /**
     * Constructor
     */
    public Banco() {
    }

    public Banco(Long idBanco, String nombre, int totalTransferencias) {
        this.idBanco = idBanco;
        this.nombre = nombre;
        this.totalTransferencias = totalTransferencias;
    }

    /**
     * Method Getter & Setter
     */
    public Long getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Long idBanco) {
        this.idBanco = idBanco;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }
}
