package com.tecsup.demo_01.controller;

import com.tecsup.demo_01.dto.DuracionRequest;
import com.tecsup.demo_01.dto.EstadoEspecialidadRequest;
import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.service.EspecialidadService;
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
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    /**
     * RF-MED-07: Registrar especialidad -> 201 Created.
     */
    @PostMapping
    public ResponseEntity<Especialidad> registrar(@Valid @RequestBody Especialidad especialidad) {
        Especialidad creada = especialidadService.registrar(especialidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Listar especialidades -> 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(especialidadService.listarTodos());
    }

    /**
     * Obtener especialidad por ID -> 200 OK / 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.buscarPorId(id));
    }

    /**
     * RF-MED-08: Modificar especialidad -> 200 OK / 404 / 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> modificar(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(especialidadService.modificar(id, especialidad));
    }

    /**
     * RF-MED-09: Activar/desactivar especialidad -> 200 OK / 404.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Especialidad> cambiarEstado(@PathVariable Long id, @RequestBody EstadoEspecialidadRequest request) {
        return ResponseEntity.ok(especialidadService.cambiarEstado(id, request.getEstado()));
    }

    /**
     * RF-MED-10: Configurar duración de consulta -> 200 OK / 404 / 400.
     */
    @PatchMapping("/{id}/duracion")
    public ResponseEntity<Especialidad> cambiarDuracion(@PathVariable Long id, @RequestBody DuracionRequest request) {
        return ResponseEntity.ok(especialidadService.cambiarDuracion(id, request.getDuracionConsulta()));
    }
}