package com.redvial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String origen;
    private String destino;
    private String salario;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    // Usuario que creó la oferta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario propietario;

    // ============================
    // NUEVO: ESTADO DE ACEPTACIÓN
    // ============================

    // Indica si la oferta ya fue aceptada
    private boolean aceptada = false;

    // Usuario que aceptó la oferta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aceptada_por")
    private Usuario aceptadaPor;

    public Oferta() {}

    // =======================
    // GETTERS & SETTERS
    // =======================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getSalario() { return salario; }
    public void setSalario(String salario) { this.salario = salario; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public Usuario getPropietario() { return propietario; }
    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public boolean isAceptada() {
        return aceptada;
    }
    public void setAceptada(boolean aceptada) {
        this.aceptada = aceptada;
    }

    public Usuario getAceptadaPor() {
        return aceptadaPor;
    }
    public void setAceptadaPor(Usuario aceptadaPor) {
        this.aceptadaPor = aceptadaPor;
    }
}
