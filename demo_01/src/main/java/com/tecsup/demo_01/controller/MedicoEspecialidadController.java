package com.tecsup.demo_01.controller;

import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.entity.MedicoEspecialidad;
import com.tecsup.demo_01.service.MedicoEspecialidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CRUD de la relación Médico <-> Especialidad.
 */
@RestController
@RequestMapping("/api")
public class MedicoEspecialidadController {

    private final MedicoEspecialidadService medicoEspecialidadService;

    public MedicoEspecialidadController(MedicoEspecialidadService medicoEspecialidadService) {
        this.medicoEspecialidadService = medicoEspecialidadService;
    }

    /**
     * Asignar una especialidad a un médico -> 201 Created / 404 / 409.
     */
    @PostMapping("/medicos/{medicoId}/especialidades/{especialidadId}")
    public ResponseEntity<MedicoEspecialidad> asignar(@PathVariable Long medicoId,
                                                      @PathVariable Long especialidadId) {
        MedicoEspecialidad creada = medicoEspecialidadService.asignar(medicoId, especialidadId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Quitar la especialidad de un médico -> 204 No Content / 404.
     */
    @DeleteMapping("/medicos/{medicoId}/especialidades/{especialidadId}")
    public ResponseEntity<Void> quitar(@PathVariable Long medicoId,
                                       @PathVariable Long especialidadId) {
        medicoEspecialidadService.quitar(medicoId, especialidadId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Listar las especialidades de un médico -> 200 OK / 404.
     */
    @GetMapping("/medicos/{medicoId}/especialidades")
    public ResponseEntity<List<Especialidad>> listarEspecialidadesDeMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(medicoEspecialidadService.listarEspecialidadesDeMedico(medicoId));
    }

    /**
     * Listar los médicos de una especialidad -> 200 OK / 404.
     */
    @GetMapping("/especialidades/{especialidadId}/medicos")
    public ResponseEntity<List<Medico>> listarMedicosDeEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(medicoEspecialidadService.listarMedicosDeEspecialidad(especialidadId));
    }
}