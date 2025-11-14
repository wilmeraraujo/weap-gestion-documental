package com.weap.gd.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "cargue_plano", schema = "cargue")
public class CarguePlano {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nombre_archivo")
	private String nombreArchivo;
	
	@Column(name = "nit_prestador")
	private String nitPrestador;
	
	@Column(name = "fecha_cargue")
	private LocalDateTime fechaCargue;
	
	@Column(name = "errores_en_cargue")
	private Boolean erroresEnCargue;
	
	@Column(name = "numero_registro")
	private Integer numeroRegistro;
	
	@JsonIgnoreProperties
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_execution_id")
	private EjecucionTarea ejecucionTarea;	
	
	private String usuario;
	
	@Column(name = "delete_at")
	private LocalDate deleteAt;

}
