package com.tecsup.demo_01.service;

import com.tecsup.demo_01.entity.EstadoMedico;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.exception.DuplicateResourceException;
import com.tecsup.demo_01.exception.ResourceNotFoundException;
import com.tecsup.demo_01.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    /**
     * RF-MED-01: Registrar médico.
     */
    public Medico registrar(Medico medico) {
        validarDuplicados(medico);
        validarEstado(medico.getEstado());
        return medicoRepository.save(medico);
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

        existente.setCodigo(datosNuevos.getCodigo());
        existente.setTipoDocumento(datosNuevos.getTipoDocumento());
        existente.setNumeroDocumento(datosNuevos.getNumeroDocumento());
        existente.setNombres(datosNuevos.getNombres());
        existente.setApellidoPaterno(datosNuevos.getApellidoPaterno());
        existente.setApellidoMaterno(datosNuevos.getApellidoMaterno());
        existente.setCmp(datosNuevos.getCmp());
        existente.setEstado(datosNuevos.getEstado());

        return medicoRepository.save(existente);
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
        return medicoRepository.save(existente);
    }

    private void validarDuplicados(Medico medico) {
        if (medicoRepository.existsByCodigo(medico.getCodigo())) {
            throw new DuplicateResourceException("Ya existe un médico con el código: " + medico.getCodigo());
        }
        if (medicoRepository.existsByNumeroDocumento(medico.getNumeroDocumento())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de documento: " + medico.getNumeroDocumento());
        }
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new DuplicateResourceException("Ya existe un médico con el CMP: " + medico.getCmp());
        }
    }

    private void validarDuplicadosExcluyendoSelf(Medico existente, Medico datosNuevos) {
        medicoRepository.findByCodigo(datosNuevos.getCodigo()).ifPresent(m -> {
            if (!m.getId().equals(existente.getId())) {
                throw new DuplicateResourceException("Ya existe un médico con el código: " + datosNuevos.getCodigo());
            }
        });
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
}