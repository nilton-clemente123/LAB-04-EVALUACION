package com.tecsup.demo_01.entity;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnore;
 
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "ESPECIALIDAD")
public class Especialidad {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;
 
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
 
    @Column(length = 255)
    private String descripcion;
 
    @Positive(message = "La duración de consulta debe ser un valor positivo mayor que cero")
    @Column(nullable = false)
    private Integer duracionConsulta;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEspecialidad estado;
 
    @JsonIgnore
    @OneToMany(mappedBy = "especialidad")
    private List<MedicoEspecialidad> medicos = new ArrayList<>();
 
    public Especialidad() {
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getCodigo() {
        return codigo;
    }
 
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
 
    public String getDescripcion() {
        return descripcion;
    }
 
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
 
    public Integer getDuracionConsulta() {
        return duracionConsulta;
    }
 
    public void setDuracionConsulta(Integer duracionConsulta) {
        this.duracionConsulta = duracionConsulta;
    }
 
    public EstadoEspecialidad getEstado() {
        return estado;
    }
 
    public void setEstado(EstadoEspecialidad estado) {
        this.estado = estado;
    }
 
    public List<MedicoEspecialidad> getMedicos() {
        return medicos;
    }
 
    public void setMedicos(List<MedicoEspecialidad> medicos) {
        this.medicos = medicos;
    }
}