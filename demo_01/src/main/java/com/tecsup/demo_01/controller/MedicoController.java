package com.tecsup.demo_01.controller;

import com.tecsup.demo_01.dto.EstadoMedicoRequest;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    /**
     * RF-MED-01: Registrar médico -> 201 Created.
     */
    @PostMapping
    public ResponseEntity<Medico> registrar(@Valid @RequestBody Medico medico) {
        Medico creado = medicoService.registrar(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * RF-MED-03: Listar médicos -> 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Medico>> listar() {
        return ResponseEntity.ok(medicoService.listarTodos());
    }

    /**
     * RF-MED-03: Obtener médico por ID -> 200 OK / 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medico> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.buscarPorId(id));
    }

    /**
     * RF-MED-02: Modificar médico -> 200 OK / 404 / 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Medico> modificar(@PathVariable Long id, @Valid @RequestBody Medico medico) {
        return ResponseEntity.ok(medicoService.modificar(id, medico));
    }

    /**
     * RF-MED-04: Activar/desactivar médico -> 200 OK / 404.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Medico> cambiarEstado(@PathVariable Long id, @RequestBody EstadoMedicoRequest request) {
        return ResponseEntity.ok(medicoService.cambiarEstado(id, request.getEstado()));
    }
}