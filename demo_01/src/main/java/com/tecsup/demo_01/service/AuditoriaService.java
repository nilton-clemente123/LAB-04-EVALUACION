package com.tecsup.demo_01.service;

import com.tecsup.demo_01.config.UsuarioActualHolder;
import com.tecsup.demo_01.entity.Auditoria;
import com.tecsup.demo_01.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final String usuarioPorDefecto;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            @Value("${auditoria.usuario:admin}") String usuarioPorDefecto) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioPorDefecto = usuarioPorDefecto;
    }

    /**
     * Registra una operación de auditoría en su propia transacción
     * (REQUIRES_NEW) para que quede persistida aunque la operación principal
     * haga rollback o falle.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Auditoria registrar(String operacion, String entidad, Object registroId) {
        String usuario = UsuarioActualHolder.get();
        if (usuario == null || usuario.isBlank()) {
            usuario = usuarioPorDefecto;
        }
        Auditoria auditoria = new Auditoria(
                usuario,
                LocalDateTime.now(),
                operacion,
                entidad,
                String.valueOf(registroId),
                null
        );
        return auditoriaRepository.save(auditoria);
    }

    /**
     * Lista la auditoría (más reciente primero).
     */
    @Transactional(readOnly = true)
    public List<Auditoria> listar() {
        return auditoriaRepository.findAllByOrderByFechaHoraDesc();
    }
}