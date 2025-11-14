package com.weap.gd.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
@Entity
@Table(name = "archivo", schema = "cargue")
public class Plano {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Pattern(
	        regexp = "^(AS|CC|CD|CE|MS|NU|PA|PE|RC|SC|TI|PT)$",
	        message = "Tipo inválido. Valores permitidos: AS, CC, CD, CE, MS, NU, PA, PE, RC, SC, TI, PT"
	    )
	@NotNull
	@Column(name = "tipo_identificacion")
	private String tipoIdentificacion;
	
	@NotNull
	@NotBlank
	@Size(max = 15, message = "La longitud del campo debe ser de maximo 20 caracteres")
	@Column(name = "numero_identificacion")
	private String numeroIdentificacion;
	
	@NotNull
	@NotBlank
	@Size(max = 20, message = "La longitud del campo debe ser de maximo 20 caracteres")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]*$", message = "El primer nombre no debe contener números ni símbolos")
	@Column(name = "primer_nombre")
	private String primerNombre;
	
	@Size(max = 20, message = "La longitud del campo debe ser de maximo 20 caracteres")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]*$", message = "El segundo nombre no debe contener números ni símbolos")
	@Column(name = "segundo_nombre")
	private String segundoNombre;
	
	@NotNull
	@NotBlank
	@Size(max = 20, message = "La longitud del campo debe ser de maximo 20 caracteres")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]*$", message = "El primer apellido no debe contener números ni símbolos")
	@Column(name = "primer_apellido")
	private String primerApellido;
	
	@Size(max = 20, message = "La longitud del campo debe ser de maximo 20 caracteres")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]*$", message = "El segundo apellido no debe contener números ni símbolos")
	@Column(name = "segundo_apellido")
	private String segundoApellido;
	
	@Column(name = "identificador_cargue")
	private Long identificadorCargue;
	
	@JsonIgnore
	private int linea;
	
}
