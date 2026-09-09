package com.tecsup.demo_01.repository;

import com.tecsup.demo_01.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    Optional<Especialidad> findByCodigo(String codigo);

    Optional<Especialidad> findByNombre(String nombre);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);
}