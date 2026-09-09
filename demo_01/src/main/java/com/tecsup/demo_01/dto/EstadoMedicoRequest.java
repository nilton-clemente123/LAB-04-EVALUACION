package com.tecsup.demo_01.dto;

import com.tecsup.demo_01.entity.EstadoMedico;

public class EstadoMedicoRequest {

    private EstadoMedico estado;

    public EstadoMedico getEstado() {
        return estado;
    }

    public void setEstado(EstadoMedico estado) {
        this.estado = estado;
    }
}