package com.tecsup.demo_01.repository;

import com.tecsup.demo_01.entity.MedicoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoEspecialidadRepository extends JpaRepository<MedicoEspecialidad, Long> {

    boolean existsByMedicoIdAndEspecialidadId(Long medicoId, Long especialidadId);

    Optional<MedicoEspecialidad> findByMedicoIdAndEspecialidadId(Long medicoId, Long especialidadId);

    List<MedicoEspecialidad> findByMedicoId(Long medicoId);

    List<MedicoEspecialidad> findByEspecialidadId(Long especialidadId);
}