package com.tecsup.demo_01.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDITORIA")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 30)
    private String operacion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(nullable = false, length = 50)
    private String registroId;

    @Column(length = 255)
    private String detalle;

    public Auditoria() {
    }

    public Auditoria(String usuario, LocalDateTime fechaHora, String operacion,
                     String entidad, String registroId, String detalle) {
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.operacion = operacion;
        this.entidad = entidad;
        this.registroId = registroId;
        this.detalle = detalle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getRegistroId() {
        return registroId;
    }

    public void setRegistroId(String registroId) {
        this.registroId = registroId;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}