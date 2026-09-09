package com.tecsup.demo_01.service;

import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.entity.EstadoEspecialidad;
import com.tecsup.demo_01.exception.DuplicateResourceException;
import com.tecsup.demo_01.exception.ResourceNotFoundException;
import com.tecsup.demo_01.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    /**
     * RF-MED-07: Registrar especialidad.
     */
    public Especialidad registrar(Especialidad especialidad) {
        validarDuplicados(especialidad);
        validarEstado(especialidad.getEstado());
        return especialidadRepository.save(especialidad);
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

        existente.setCodigo(datosNuevos.getCodigo());
        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setDuracionConsulta(datosNuevos.getDuracionConsulta());
        existente.setEstado(datosNuevos.getEstado());

        return especialidadRepository.save(existente);
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
        return especialidadRepository.save(existente);
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
        return especialidadRepository.save(existente);
    }

    private void validarDuplicados(Especialidad especialidad) {
        if (especialidadRepository.existsByCodigo(especialidad.getCodigo())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el código: " + especialidad.getCodigo());
        }
        if (especialidadRepository.existsByNombre(especialidad.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre: " + especialidad.getNombre());
        }
    }

    private void validarDuplicadosExcluyendoSelf(Especialidad existente, Especialidad datosNuevos) {
        especialidadRepository.findByCodigo(datosNuevos.getCodigo()).ifPresent(e -> {
            if (!e.getId().equals(existente.getId())) {
                throw new DuplicateResourceException("Ya existe una especialidad con el código: " + datosNuevos.getCodigo());
            }
        });
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
}