package com.tecsup.demo_01.config;

import com.tecsup.demo_01.entity.Especialidad;
import com.tecsup.demo_01.entity.EstadoEspecialidad;
import com.tecsup.demo_01.entity.EstadoMedico;
import com.tecsup.demo_01.entity.Medico;
import com.tecsup.demo_01.entity.MedicoEspecialidad;
import com.tecsup.demo_01.repository.EspecialidadRepository;
import com.tecsup.demo_01.repository.MedicoEspecialidadRepository;
import com.tecsup.demo_01.repository.MedicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final MedicoEspecialidadRepository medicoEspecialidadRepository;

    public DataInitializer(EspecialidadRepository especialidadRepository,
                           MedicoRepository medicoRepository,
                           MedicoEspecialidadRepository medicoEspecialidadRepository) {
        this.especialidadRepository = especialidadRepository;
        this.medicoRepository = medicoRepository;
        this.medicoEspecialidadRepository = medicoEspecialidadRepository;
    }

    @Override
    public void run(String... args) {
        inicializarEspecialidades();
        inicializarMedicos();
    }

    private void inicializarEspecialidades() {
        if (especialidadRepository.count() > 0) {
            return;
        }

        Especialidad cardiologia = new Especialidad();
        cardiologia.setCodigo("ESP-001");
        cardiologia.setNombre("Cardiología");
        cardiologia.setDescripcion("Atención de enfermedades del corazón y sistema cardiovascular");
        cardiologia.setDuracionConsulta(30);
        cardiologia.setEstado(EstadoEspecialidad.ACTIVA);

        Especialidad pediatria = new Especialidad();
        pediatria.setCodigo("ESP-002");
        pediatria.setNombre("Pediatría");
        pediatria.setDescripcion("Atención médica de niños y adolescentes");
        pediatria.setDuracionConsulta(20);
        pediatria.setEstado(EstadoEspecialidad.ACTIVA);

        Especialidad medicinaInterna = new Especialidad();
        medicinaInterna.setCodigo("ESP-003");
        medicinaInterna.setNombre("Medicina Interna");
        medicinaInterna.setDescripcion("Diagnóstico y tratamiento de enfermedades del adulto");
        medicinaInterna.setDuracionConsulta(30);
        medicinaInterna.setEstado(EstadoEspecialidad.ACTIVA);

        Especialidad dermatologia = new Especialidad();
        dermatologia.setCodigo("ESP-004");
        dermatologia.setNombre("Dermatología");
        dermatologia.setDescripcion("Atención de enfermedades de la piel");
        dermatologia.setDuracionConsulta(25);
        dermatologia.setEstado(EstadoEspecialidad.ACTIVA);

        especialidadRepository.saveAll(List.of(cardiologia, pediatria, medicinaInterna, dermatologia));
    }

    private void inicializarMedicos() {
        if (medicoRepository.count() > 0) {
            return;
        }

        Medico medico1 = new Medico();
        medico1.setCodigo("MED-001");
        medico1.setTipoDocumento("DNI");
        medico1.setNumeroDocumento("12345678");
        medico1.setNombres("Juan Carlos");
        medico1.setApellidoPaterno("García");
        medico1.setApellidoMaterno("López");
        medico1.setCmp("CMP-11111");
        medico1.setEstado(EstadoMedico.ACTIVO);

        Medico medico2 = new Medico();
        medico2.setCodigo("MED-002");
        medico2.setTipoDocumento("DNI");
        medico2.setNumeroDocumento("87654321");
        medico2.setNombres("María Fernanda");
        medico2.setApellidoPaterno("Quispe");
        medico2.setApellidoMaterno("Rojas");
        medico2.setCmp("CMP-22222");
        medico2.setEstado(EstadoMedico.ACTIVO);

        Medico medico3 = new Medico();
        medico3.setCodigo("MED-003");
        medico3.setTipoDocumento("DNI");
        medico3.setNumeroDocumento("11223344");
        medico3.setNombres("Pedro Luis");
        medico3.setApellidoPaterno("Sánchez");
        medico3.setApellidoMaterno("Vargas");
        medico3.setCmp("CMP-33333");
        medico3.setEstado(EstadoMedico.INACTIVO);

        medicoRepository.saveAll(List.of(medico1, medico2, medico3));

        // Relación de ejemplo: MED-001 -> Cardiología y Medicina Interna
        Especialidad cardiologia = especialidadRepository.findByCodigo("ESP-001").orElse(null);
        Especialidad medicinaInterna = especialidadRepository.findByCodigo("ESP-003").orElse(null);

        if (cardiologia != null) {
            medicoEspecialidadRepository.save(new MedicoEspecialidad(medico1, cardiologia));
        }
        if (medicinaInterna != null) {
            medicoEspecialidadRepository.save(new MedicoEspecialidad(medico1, medicinaInterna));
        }
    }
}