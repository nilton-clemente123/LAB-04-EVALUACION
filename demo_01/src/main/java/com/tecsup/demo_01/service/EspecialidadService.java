package com.tecsup.demo_01.service;

import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.entity.EstadoEspecialidad;
import com.tecsup.demo_01.exception.DuplicateResourceException;
import com.tecsup.demo_01.exception.ResourceNotFoundException;
import com.tecsup.demo_01.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final AuditoriaService auditoriaService;

    public EspecialidadService(EspecialidadRepository especialidadRepository, AuditoriaService auditoriaService) {
        this.especialidadRepository = especialidadRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * RF-MED-07: Registrar especialidad.
     */
    @Transactional
    public Especialidad registrar(Especialidad especialidad) {
        validarDuplicados(especialidad);
        validarEstado(especialidad.getEstado());
        Especialidad guardada = especialidadRepository.save(especialidad);
        guardada.setCodigo(generarCodigoEspecialidad(guardada.getId()));
        guardada = especialidadRepository.save(guardada);
        auditoriaService.registrar("REGISTRO", "ESPECIALIDAD", guardada.getId());
        return guardada;
    }

    /**
     * Listar especialidades.
     */
    public List<Especialidad> listarTodos() {
        return especialidadRepository.findAll();
    }

    /**
     * Buscar especialidad por ID.
     */
    public Especialidad buscarPorId(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con ID: " + id));
    }

    /**
     * RF-MED-08: Modificar especialidad.
     */
    public Especialidad modificar(Long id, Especialidad datosNuevos) {
        Especialidad existente = buscarPorId(id);
        validarDuplicadosExcluyendoSelf(existente, datosNuevos);
        validarEstado(datosNuevos.getEstado());

        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setDuracionConsulta(datosNuevos.getDuracionConsulta());
        existente.setEstado(datosNuevos.getEstado());

        Especialidad guardada = especialidadRepository.save(existente);
        auditoriaService.registrar("MODIFICACION", "ESPECIALIDAD", guardada.getId());
        return guardada;
    }

    /**
     * RF-MED-09: Activar/desactivar especialidad.
     */
    public Especialidad cambiarEstado(Long id, EstadoEspecialidad nuevoEstado) {
        Especialidad existente = buscarPorId(id);
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado debe ser ACTIVA o INACTIVA");
        }
        existente.setEstado(nuevoEstado);
        Especialidad guardada = especialidadRepository.save(existente);
        auditoriaService.registrar("MODIFICACION", "ESPECIALIDAD", guardada.getId());
        return guardada;
    }

    /**
     * RF-MED-10: Configurar duración de consulta.
     */
    public Especialidad cambiarDuracion(Long id, Integer duracion) {
        Especialidad existente = buscarPorId(id);
        if (duracion == null || duracion <= 0) {
            throw new IllegalArgumentException("La duración de consulta debe ser un valor positivo mayor que cero");
        }
        existente.setDuracionConsulta(duracion);
        Especialidad guardada = especialidadRepository.save(existente);
        auditoriaService.registrar("MODIFICACION", "ESPECIALIDAD", guardada.getId());
        return guardada;
    }

    /**
     * Elimina una especialidad (borrado físico).
     * Verificación de integridad: lanza 404 si no existe y, gracias a
     * cascade = ALL + orphanRemoval = true en Especialidad.medicos,
     * elimina automáticamente sus relaciones MedicoEspecialidad.
     */
    @Transactional
    public void eliminar(Long id) {
        Especialidad especialidad = buscarPorId(id);
        especialidadRepository.delete(especialidad);
        auditoriaService.registrar("ELIMINACION", "ESPECIALIDAD", especialidad.getId());
    }

    private void validarDuplicados(Especialidad especialidad) {
        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre: " + especialidad.getNombre());
        }
    }

    private void validarDuplicadosExcluyendoSelf(Especialidad existente, Especialidad datosNuevos) {
        especialidadRepository.findByNombre(datosNuevos.getNombre()).ifPresent(e -> {
            if (!e.getId().equals(existente.getId())) {
                throw new DuplicateResourceException("Ya existe una especialidad con el nombre: " + datosNuevos.getNombre());
            }
        });
    }

    private void validarEstado(EstadoEspecialidad estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado debe ser ACTIVA o INACTIVA");
        }
    }

    private String generarCodigoEspecialidad(Long id) {
        return "ESP-" + String.format("%03d", id);
    }
}