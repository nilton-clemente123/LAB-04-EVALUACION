package com.tecsup.demo_01.dto;

import com.tecsup.demo_01.entity.EstadoEspecialidad;

public class EstadoEspecialidadRequest {

    private EstadoEspecialidad estado;

    public EstadoEspecialidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoEspecialidad estado) {
        this.estado = estado;
    }
}