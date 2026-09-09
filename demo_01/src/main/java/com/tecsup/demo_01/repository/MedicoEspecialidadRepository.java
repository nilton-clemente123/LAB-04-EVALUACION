package com.tecsup.demo_01.repository;

import com.tecsup.demo_01.entity.MedicoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoEspecialidadRepository extends JpaRepository<MedicoEspecialidad, Long> {
}