package com.tecsup.demo_01.service;

import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.entity.MedicoEspecialidad;
import com.tecsup.demo_01.exception.DuplicateResourceException;
import com.tecsup.demo_01.exception.ResourceNotFoundException;
import com.tecsup.demo_01.repository.EspecialidadRepository;
import com.tecsup.demo_01.repository.MedicoEspecialidadRepository;
import com.tecsup.demo_01.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoEspecialidadService {

    private final MedicoEspecialidadRepository medicoEspecialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final AuditoriaService auditoriaService;

    public MedicoEspecialidadService(MedicoEspecialidadRepository medicoEspecialidadRepository,
                                     MedicoRepository medicoRepository,
                                     EspecialidadRepository especialidadRepository,
                                     AuditoriaService auditoriaService) {
        this.medicoEspecialidadRepository = medicoEspecialidadRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Asigna una especialidad a un médico.
     * 404 si el médico o la especialidad no existen; 409 si la relación ya existe.
     */
    @Transactional
    public MedicoEspecialidad asignar(Long medicoId, Long especialidadId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + medicoId));
        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con ID: " + especialidadId));

        if (medicoEspecialidadRepository.existsByMedicoIdAndEspecialidadId(medicoId, especialidadId)) {
            throw new DuplicateResourceException("El médico ya tiene asignada esta especialidad.");
        }

        MedicoEspecialidad relacion = medicoEspecialidadRepository.save(new MedicoEspecialidad(medico, especialidad));
        auditoriaService.registrar("REGISTRO", "MEDICO_ESPECIALIDAD", relacion.getId());
        return relacion;
    }

    /**
     * Quita la especialidad asignada a un médico.
     * 404 si la relación no existe.
     */
    @Transactional
    public void quitar(Long medicoId, Long especialidadId) {
        MedicoEspecialidad relacion = medicoEspecialidadRepository
                .findByMedicoIdAndEspecialidadId(medicoId, especialidadId)
                .orElseThrow(() -> new ResourceNotFoundException("El médico no tiene asignada esa especialidad."));

        Long relacionId = relacion.getId();
        medicoEspecialidadRepository.delete(relacion);
        auditoriaService.registrar("ELIMINACION", "MEDICO_ESPECIALIDAD", relacionId);
    }

    /**
     * Lista las especialidades asignadas a un médico.
     * 404 si el médico no existe.
     */
    @Transactional(readOnly = true)
    public List<Especialidad> listarEspecialidadesDeMedico(Long medicoId) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new ResourceNotFoundException("Médico no encontrado con ID: " + medicoId);
        }
        return medicoEspecialidadRepository.findByMedicoId(medicoId).stream()
                .map(MedicoEspecialidad::getEspecialidad)
                .collect(Collectors.toList());
    }

    /**
     * Lista los médicos que tienen una especialidad.
     * 404 si la especialidad no existe.
     */
    @Transactional(readOnly = true)
    public List<Medico> listarMedicosDeEspecialidad(Long especialidadId) {
        if (!especialidadRepository.existsById(especialidadId)) {
            throw new ResourceNotFoundException("Especialidad no encontrada con ID: " + especialidadId);
        }
        return medicoEspecialidadRepository.findByEspecialidadId(especialidadId).stream()
                .map(MedicoEspecialidad::getMedico)
                .collect(Collectors.toList());
    }
}