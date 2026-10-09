package com.tecsup.demo_01.service;

import com.tecsup.demo_01.entity.EstadoMedico;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.exception.DuplicateResourceException;
import com.tecsup.demo_01.exception.ResourceNotFoundException;
import com.tecsup.demo_01.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final AuditoriaService auditoriaService;

    public MedicoService(MedicoRepository medicoRepository, AuditoriaService auditoriaService) {
        this.medicoRepository = medicoRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * RF-MED-01: Registrar médico.
     */
    @Transactional
    public Medico registrar(Medico medico) {
        validarDuplicados(medico);
        validarEstado(medico.getEstado());
        Medico guardado = medicoRepository.save(medico);
        guardado.setCodigo(generarCodigoMedico(guardado.getId()));
        guardado = medicoRepository.save(guardado);
        auditoriaService.registrar("REGISTRO", "MEDICO", guardado.getId());
        return guardado;
    }

    /**
     * RF-MED-03: Listar médicos.
     */
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    /**
     * RF-MED-03: Buscar médico por ID.
     */
    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));
    }

    /**
     * RF-MED-02: Modificar datos de un médico.
     */
    public Medico modificar(Long id, Medico datosNuevos) {
        Medico existente = buscarPorId(id);
        validarDuplicadosExcluyendoSelf(existente, datosNuevos);
        validarEstado(datosNuevos.getEstado());

        existente.setTipoDocumento(datosNuevos.getTipoDocumento());
        existente.setNumeroDocumento(datosNuevos.getNumeroDocumento());
        existente.setNombres(datosNuevos.getNombres());
        existente.setApellidoPaterno(datosNuevos.getApellidoPaterno());
        existente.setApellidoMaterno(datosNuevos.getApellidoMaterno());
        existente.setCmp(datosNuevos.getCmp());
        existente.setEstado(datosNuevos.getEstado());

        Medico guardado = medicoRepository.save(existente);
        auditoriaService.registrar("MODIFICACION", "MEDICO", guardado.getId());
        return guardado;
    }

    /**
     * RF-MED-04: Activar/desactivar médico (cambiar estado).
     */
    public Medico cambiarEstado(Long id, EstadoMedico nuevoEstado) {
        Medico existente = buscarPorId(id);
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        existente.setEstado(nuevoEstado);
        Medico guardado = medicoRepository.save(existente);
        auditoriaService.registrar("MODIFICACION", "MEDICO", guardado.getId());
        return guardado;
    }

    /**
     * Elimina un médico (borrado físico).
     * Verificación de integridad: lanza 404 si no existe y, gracias a
     * cascade = ALL + orphanRemoval = true en Medico.especialidades,
     * elimina automáticamente sus relaciones MedicoEspecialidad.
     */
    @Transactional
    public void eliminar(Long id) {
        Medico medico = buscarPorId(id);
        medicoRepository.delete(medico);
        auditoriaService.registrar("ELIMINACION", "MEDICO", medico.getId());
    }

    private void validarDuplicados(Medico medico) {
        if (medicoRepository.existsByNumeroDocumento(medico.getNumeroDocumento())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de documento: " + medico.getNumeroDocumento());
        }
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new DuplicateResourceException("Ya existe un médico con el CMP: " + medico.getCmp());
        }
    }

    private void validarDuplicadosExcluyendoSelf(Medico existente, Medico datosNuevos) {
        medicoRepository.findByNumeroDocumento(datosNuevos.getNumeroDocumento()).ifPresent(m -> {
            if (!m.getId().equals(existente.getId())) {
                throw new DuplicateResourceException("Ya existe un médico con el número de documento: " + datosNuevos.getNumeroDocumento());
            }
        });
        medicoRepository.findByCmp(datosNuevos.getCmp()).ifPresent(m -> {
            if (!m.getId().equals(existente.getId())) {
                throw new DuplicateResourceException("Ya existe un médico con el CMP: " + datosNuevos.getCmp());
            }
        });
    }

    private void validarEstado(EstadoMedico estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
    }

    private String generarCodigoMedico(Long id) {
        return "MED-" + String.format("%03d", id);
    }
}