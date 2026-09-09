package com.tecsup.demo_01.repository;

import com.tecsup.demo_01.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByCodigo(String codigo);

    Optional<Medico> findByNumeroDocumento(String numeroDocumento);

    Optional<Medico> findByCmp(String cmp);

    boolean existsByCodigo(String codigo);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByCmp(String cmp);
}